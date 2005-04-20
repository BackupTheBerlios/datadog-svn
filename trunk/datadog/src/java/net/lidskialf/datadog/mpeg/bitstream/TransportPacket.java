/*
 * Copyright (C) 2005 Andrew de Quincey <adq_dvb@lidskialf.net>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package net.lidskialf.datadog.mpeg.bitstream;

/**
 * Class representing a Transport Stream Packet.
 */
public class TransportPacket {

    public static final int adapflag_discontinuity_indicator = 0x80;

    public static final int adapflag_random_access_indicator = 0x40;

    public static final int adapflag_elementary_stream_priority_indicator = 0x20;

    public static final int adapflag_PCR_flag = 0x10;

    public static final int adapflag_OPCR_flag = 0x08;

    public static final int adapflag_splicing_point_flag = 0x04;

    public static final int adapflag_transport_private_data_flag = 0x02;

    public static final int adapflag_adaptation_field_extension_flag = 0x01;

    public static final int adapextenflag_ltw_flag = 0x80;

    public static final int adapextenflag_piecewise_rate_flag = 0x40;

    public static final int adapextenflag_seamless_splice_flag = 0x20;

    public static final int TS_PACKET_LEN = 188;

    public TransportPacket(byte[] data, long streamPosition) {
        if (data.length != TS_PACKET_LEN) {
            throw new RuntimeException("Invalid length of transport stream packet");
        }
        this.data = data;
        this.streamPosition = streamPosition;

        // FIXME: need to deal with errors!
    }

    /**
     * Is the transport error flag set?
     *
     * @return True if it is.
     */
    public boolean transport_error_indicator() {
        return (data[1] & 0x80) != 0;
    }

    /**
     * Is this a payload start?
     *
     * @return True if it is.
     */
    public boolean payload_unit_start_indicator() {
        return (data[1] & 0x40) != 0;
    }

    /**
     * Is the transport priority bit set?
     *
     * @return True if it is.
     */
    public boolean transport_priority() {
        return (data[1] & 0x20) != 0;
    }

    /**
     * The pid of this packet.
     *
     * @return The pid.
     */
    public int pid() {
        return ((data[1] & 0x1f) << 8) | (data[2] & 0xff);
    }

    /**
     * The transport scrambling control field.
     *
     * @return The two bit field..
     */
    public int transport_scrambling_control() {
        return (data[3] & 0xc0) >> 6;
    }

    /**
     * Packet continuity counter.
     *
     * @return The 4 bit continuity counter.
     */
    public int continuity_counter() {
        return data[3] & 0x0f;
    }

    /**
     * The adaptation_field_length value.
     *
     * @return The value.
     */
    public int adaptation_field_length() {
        if (!hasAdaptation())
            return 0;
        return data[4] & 0xff;
    }

    /**
     * adaptation_field flags byte.
     *
     * @return The flags byte.
     */
    public int adaptation_flags() {
        if (!hasAdaptation())
            return 0;
        if (data[4] < 1)
            return 0;
        return data[5] & 0xff;
    }

    /**
     * Does the packet have a payload?
     *
     * @return True if it does.
     */
    public boolean hasPayload() {
        return (data[3] & 0x10) != 0;
    }

    /**
     * Does the packet have an adaptation field?
     *
     * @return True if it does.
     */
    public boolean hasAdaptation() {
        return (data[3] & 0x20) != 0;
    }

    /**
     * Does the packet have an adaptation_extension?
     *
     * @return True if it does.
     */
    public boolean hasAdaptationExtension() {
        if (!hasAdaptation())
            return false;
        return (adaptation_flags() & adapflag_adaptation_field_extension_flag) != 0;
    }

    /**
     * Flags from the adaptation_extension field.
     *
     * @return The flags (including the 5 bit reserved values).
     */
    public int adaptation_extension_flags() {
        if (!hasAdaptationExtension())
            return 0;

        int pos = adaptationOffset(adapflag_PCR_flag | adapflag_OPCR_flag | adapflag_splicing_point_flag | adapflag_transport_private_data_flag);
        int len = data[pos + 0] & 0xff;
        if (len < 1)
            return 0;
        return data[pos + 1];
    }

    /**
     * PCR of this packet.
     *
     * @return The PCR.
     */
    public long PCR() {
        if ((adaptation_flags() & adapflag_PCR_flag) == 0)
            return 0;
        int pos = adaptationOffset(0);

        long pcrbase = (data[pos + 0] & 0xff) << 25;
        pcrbase |= (data[pos + 1] & 0xff) << 17;
        pcrbase |= (data[pos + 2] & 0xff) << 9;
        pcrbase |= (data[pos + 3] & 0xff) << 1;
        pcrbase |= (data[pos + 4] & 0x80) >> 7;

        long pcrext = (data[pos + 4] & 0x01) << 8;
        pcrext |= (data[pos + 5] & 0xff);

        return (pcrbase * 300) + pcrext;
    }

    /**
     * OPCR of this packet.
     *
     * @return The OPCR.
     */
    public long OPCR() {
        if ((adaptation_flags() & adapflag_OPCR_flag) == 0)
            return 0;
        int pos = adaptationOffset(adapflag_PCR_flag);

        long pcrbase = (data[pos + 0] & 0xff) << 25;
        pcrbase |= (data[pos + 1] & 0xff) << 17;
        pcrbase |= (data[pos + 2] & 0xff) << 9;
        pcrbase |= (data[pos + 3] & 0xff) << 1;
        pcrbase |= (data[pos + 4] & 0x80) >> 7;

        long pcrext = (data[pos + 4] & 0x01) << 8;
        pcrext |= (data[pos + 5] & 0xff);

        return (pcrbase * 300) + pcrext;
    }

    /**
     * Splice countdown value.
     *
     * @return Value of the splice countdown field.
     */
    public int splice_countdown() {
        if ((adaptation_flags() & adapflag_splicing_point_flag) == 0)
            return 0;
        int pos = adaptationOffset(adapflag_PCR_flag | adapflag_OPCR_flag);

        return (data[pos + 0] & 0xff);
    }

    /**
     * The private data from the adaptation field.
     *
     * @return The data.
     */
    public byte[] private_data() {
        if ((adaptation_flags() & adapflag_transport_private_data_flag) == 0)
            return new byte[0];
        int pos = adaptationOffset(adapflag_PCR_flag | adapflag_OPCR_flag | adapflag_splicing_point_flag);
        int len = data[pos + 0] & 0xff;

        byte[] tmp = new byte[len];
        System.arraycopy(data, pos + 1, tmp, 0, len);
        return tmp;
    }

    /**
     * The ltw_offset field.
     *
     * @return >=0 if valid, -1 if not.
     */
    public int ltw_offset() {
        if ((adaptation_extension_flags() & adapextenflag_ltw_flag) == 0)
            return 0;
        int pos = adaptationExtensionOffset(0);

        if ((data[pos + 0] & 0x80) == 0)
            return -1; // i.e. not valid
        return ((data[pos + 0] & 0x7f) << 8) | (data[pos + 1] & 0xff);
    }

    /**
     * The piecewise rate field.
     *
     * @return The value.
     */
    public int piecewise_rate() {
        if ((adaptation_extension_flags() & adapextenflag_piecewise_rate_flag) == 0)
            return 0;
        int pos = adaptationExtensionOffset(adapextenflag_ltw_flag);

        return ((data[pos + 0] & 0x3f) << 16) | ((data[pos + 1] & 0xff) << 8) | (data[pos + 2] & 0xff);
    }

    /**
     * Seamless splice type.
     *
     * @return The type.
     */
    public int seamless_splice_type() {
        if ((adaptation_extension_flags() & adapextenflag_seamless_splice_flag) == 0)
            return 0;
        int pos = adaptationExtensionOffset(adapextenflag_ltw_flag | adapextenflag_piecewise_rate_flag);

        return (data[pos + 0] & 0xf0) >> 4;
    }

    /**
     * DTS_next_AU field.
     *
     * @return The value.
     */
    public long DTS_next_AU() {
        if ((adaptation_extension_flags() & adapextenflag_seamless_splice_flag) == 0)
            return 0;
        int pos = adaptationExtensionOffset(adapextenflag_ltw_flag | adapextenflag_piecewise_rate_flag);

        long result = (data[pos + 0] & 0x0e) << 30;
        result |= (data[pos + 1] & 0xff) << 22;
        result |= (data[pos + 2] & 0xfe) << 14;
        result |= (data[pos + 3] & 0xff) << 7;
        result |= (data[pos + 4] & 0xfe) >> 1;

        return result;
    }

    /**
     * Accessor for any reserved bytes following the adaptation extension field.
     *
     * @return The array of bytes
     */
    public byte[] reserved_bytes() {
        if (!hasAdaptationExtension())
            return new byte[0];

        int adapExtenStart = adaptationOffset(adapflag_PCR_flag | adapflag_OPCR_flag | adapflag_splicing_point_flag | adapflag_transport_private_data_flag);
        int reservedStart = adaptationExtensionOffset(adapextenflag_ltw_flag | adapextenflag_piecewise_rate_flag | adapextenflag_seamless_splice_flag);
        int used = reservedStart - adapExtenStart;
        int unused = (data[adapExtenStart] & 0xff) - used;

        if (unused > 0) {
            byte[] result = new byte[unused];
            System.arraycopy(data, reservedStart, result, 0, unused);
            return result;
        }
        return new byte[0];
    }

    /**
     * Accessor for the stuffing bytes at the end of the adaptation field.
     *
     * @return The byte array.
     */
    public byte[] stuffing_bytes() {
        if (!hasAdaptation())
            return new byte[0];

        int pos = adaptationOffset(adapflag_PCR_flag | adapflag_OPCR_flag | adapflag_splicing_point_flag | adapflag_transport_private_data_flag
                | adapflag_adaptation_field_extension_flag);
        int len = (adaptation_field_length() + 4 + 1 + 1) - pos;

        byte[] tmp = new byte[len];
        System.arraycopy(data, pos, tmp, 0, len);
        return tmp;
    }

    /**
     * Get the size of the payload of this packet.
     *
     * @return The payload size in bytes.
     */
    public int payloadSize() {
        return data.length - (4 + adaptation_field_length());
    }

    /**
     * Retrieve a chunk of payload data from this packet.
     *
     * @param payloadOff
     *            Offset into the payload.
     * @param dest
     *            Buffer to put the data in.
     * @param destOff
     *            Offset in dest to put the data.
     * @param length
     *            Number of bytes to extract.
     */
    public void getPayloadData(int payloadOff, byte[] dest, int destOff, int length) {
        System.arraycopy(data, payloadOff, dest, destOff, length);
    }

    /**
     * Calculate an offset into the adaptation field.
     *
     * @param flags
     *            Bitmask of the values to skip if present.
     * @return Position of the field after those skipped.
     */
    private int adaptationOffset(int flags) {

        if (!hasAdaptation())
            throw new RuntimeException("No adaptation field present");

        flags &= adaptation_flags();

        int pos = 4 + 1 + 1;
        if ((flags & adapflag_PCR_flag) != 0)
            pos += 6;
        if ((flags & adapflag_OPCR_flag) != 0)
            pos += 6;
        if ((flags & adapflag_splicing_point_flag) != 0)
            pos += 1;
        if ((flags & adapflag_transport_private_data_flag) != 0) {
            pos += (data[pos] & 0xff) + 1;
        }
        if ((flags & adapflag_adaptation_field_extension_flag) != 0) {
            pos += (data[pos] & 0xff) + 1;
        }

        return pos;
    }

    /**
     * Calculate an offset into the adaptation extension field.
     *
     * @param flags
     *            Bitmask of the values to skip if present.
     * @return Position of the field after those skipped.
     */
    private int adaptationExtensionOffset(int flags) {
        if (!hasAdaptation())
            throw new RuntimeException("No adaptation field present");
        if ((adaptation_flags() & adapflag_adaptation_field_extension_flag) == 0)
            throw new RuntimeException("No adaptation extension field present");

        int pos = adaptationOffset(adapflag_PCR_flag | adapflag_OPCR_flag | adapflag_splicing_point_flag | adapflag_transport_private_data_flag);

        flags &= adaptation_extension_flags();

        pos += 1 + 1;

        if ((flags & adapextenflag_ltw_flag) != 0)
            pos += 2;
        if ((flags & adapextenflag_piecewise_rate_flag) != 0)
            pos += 3;
        if ((flags & adapextenflag_seamless_splice_flag) != 0)
            pos += 5;

        return pos;
    }

    /**
     * The raw data itself (finally!)
     */
    private byte[] data;

    /**
     * Position of packet within the source stream.
     */
    private long streamPosition;
}
