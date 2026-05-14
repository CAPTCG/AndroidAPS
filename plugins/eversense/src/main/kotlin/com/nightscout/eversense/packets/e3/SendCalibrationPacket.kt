package com.nightscout.eversense.packets.e3

import com.nightscout.eversense.enums.EversenseSecurityType
import com.nightscout.eversense.packets.EversenseBasePacket
import com.nightscout.eversense.packets.EversensePacket
import com.nightscout.eversense.packets.e3.util.EversenseE3Writer

/**
 * Sends a blood glucose calibration value to the Eversense E3 transmitter.
 *
 * Packet structure verified against official Eversense app
 * (decompiled from operationToSendBloodGlucoseValueToTransmitter):
 *
 * [0]    = 0x15 (21) — command ID, prepended by buildRequest()
 * [1-2]  = sampleDate — date of the BG measurement (2 bytes, FAT packed)
 * [3-4]  = sampleTime — time of the BG measurement (2 bytes, FAT packed)
 * [5-6]  = currentTime — time of submission = now (2 bytes, FAT packed, NOT date)
 * [7]    = glucoseMgDl raw value (low byte when <=255)
 * [8]    = BG value MSB  (data16BitsFromIntLSByteFirst[1])
 * [9]    = BG value LSB  (data16BitsFromIntLSByteFirst[0])
 * [10]   = 0x00 — rolling cal disabled; official app only enables this for US+protocolVersion>=4.0
 * [11-12]= CRC16 LSB first, appended by buildRequest()
 *
 * @param glucoseMgDl  Blood glucose value in mg/dL
 * @param sampleTimeMs Timestamp of the BG measurement (defaults to now)
 */
@EversensePacket(
    requestId = EversenseE3Packets.SendBloodGlucoseDataCommandId,
    responseId = EversenseE3Packets.SendBloodGlucoseDataResponseId,
    typeId = 0,
    securityType = EversenseSecurityType.None
)
class SendCalibrationPacket(
    private val glucoseMgDl: Int,
    private val sampleTimeMs: Long = System.currentTimeMillis()
) : EversenseBasePacket() {

    override fun getRequestData(): ByteArray {
        val now = System.currentTimeMillis()

        val sampleDate = EversenseE3Writer.writeDate(sampleTimeMs)
        val sampleTime = EversenseE3Writer.writeTime(sampleTimeMs)
        val currentTime = EversenseE3Writer.writeTime(now)  // official app sends current TIME, not date

        // Official app uses data16BitsFromIntLSByteFirst: [LSB, MSB]
        // Byte [7] = raw glucose value (LSB when <=255), [8] = MSB, [9] = LSB
        val bgLsb = (glucoseMgDl and 0xFF).toByte()
        val bgMsb = ((glucoseMgDl shr 8) and 0xFF).toByte()

        return byteArrayOf(
            sampleDate[0], sampleDate[1],   // [1-2] sample date
            sampleTime[0], sampleTime[1],   // [3-4] sample time
            currentTime[0], currentTime[1], // [5-6] current submission time (NOT date)
            bgLsb,                          // [7]   BG raw value (LSB)
            bgMsb,                          // [8]   BG MSB
            bgLsb,                          // [9]   BG LSB
            0x00.toByte()                   // [10]  rolling calibration disabled — matches non-US official app
        )
    }

    override fun parseResponse(): Response? {
        if (receivedData.isEmpty()) return null
        return Response()
    }

    class Response : EversenseBasePacket.Response()
}
