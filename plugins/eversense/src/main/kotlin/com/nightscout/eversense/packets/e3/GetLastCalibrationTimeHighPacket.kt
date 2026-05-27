package com.nightscout.eversense.packets.e3

import com.nightscout.eversense.enums.EversenseE3Memory
import com.nightscout.eversense.enums.EversenseSecurityType
import com.nightscout.eversense.packets.EversenseBasePacket
import com.nightscout.eversense.packets.EversensePacket

@EversensePacket(
    requestId = EversenseE3Packets.ReadSingleByteSerialFlashRegisterCommandId,
    responseId = EversenseE3Packets.ReadSingleByteSerialFlashRegisterResponseId,
    typeId = 0,
    securityType = EversenseSecurityType.None
)
class GetLastCalibrationTimeHighPacket : EversenseBasePacket() {

    override fun getRequestData(): ByteArray {
        return EversenseE3Memory.LastCalibrationTimeHigh.getRequestData()
    }

    override fun parseResponse(): Response? {
        if (receivedData.isEmpty()) return null
        return Response(highByte = receivedData[getStartIndex()].toInt() and 0xFF)
    }

    data class Response(val highByte: Int) : EversenseBasePacket.Response()
}