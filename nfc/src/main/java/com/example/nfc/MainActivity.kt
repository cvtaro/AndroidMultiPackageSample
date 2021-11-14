package com.example.nfc

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val TAG = "[dbg nfc]"

    private lateinit var mNfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate()")

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")

        mNfcAdapter.enableReaderMode(
            this,
            mReaderCallback,
            NfcAdapter.FLAG_READER_NFC_B,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")

        mNfcAdapter.disableReaderMode(this@MainActivity)
    }

    private val mReaderCallback = object : NfcAdapter.ReaderCallback {
        override fun onTagDiscovered(tag: Tag) {
            Log.d(TAG, "onTagDiscovered()")
            Log.d(TAG, "tag id    = ${byteArray2HexString(tag.id)}")
            for (tech in tag.techList) {
                Log.d(TAG, "tag techs = ${tech}")
            }

            // isoDep でAPDUを送信
            val isoDep = IsoDep.get(tag)
            isoDep.connect()
            // ここから処理を書いていく
            var commandResult = isoDep.transceive(mSelectCommand)
            Log.d(TAG, "selectCommand       = ${byteArray2HexString(mSelectCommand)}")
            Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
//            if (commandResult[0] == 0x90.toByte()) {
//                Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
//            }
            commandResult = isoDep.transceive(mSelectCommandEF01)
            Log.d(TAG, "selectCommand       = ${byteArray2HexString(mSelectCommandEF01)}")
            Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
//            if (commandResult[0] == 0x90.toByte()) {
//                Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
//            }
            commandResult = isoDep.transceive(mReadBinaryCommandEF01)
            Log.d(TAG, "selectCommand       = ${byteArray2HexString(mReadBinaryCommandEF01)}")
            Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
            if (commandResult[commandResult.size-2] == 0x90.toByte()) {
                val publisherDataLength = commandResult[1].toInt()
                val publisherDataBinary = commandResult.copyOfRange(2, 2 + publisherDataLength)
                val version = publisherDataBinary.copyOfRange(0, 3).toString(charset("sjis"))
                val publishDate = publisherDataBinary.copyOfRange(3, 7).joinToString(separator = "") { "%02x".format(it) }
                val effectiveDate = publisherDataBinary.copyOfRange(7, 11).joinToString(separator = "") { "%02x".format(it) }
                Log.d(TAG, " - publisherDataLength = ${publisherDataLength}")
                Log.d(TAG, " - publisherDataBinary = ${byteArray2HexString(publisherDataBinary)}")
                Log.d(TAG, " - version             = ${version}")
                Log.d(TAG, " - publishDate         = ${publishDate}")
                Log.d(TAG, " - effectiveDate       = ${effectiveDate}")
            }
            commandResult = isoDep.transceive(mRetryCountVerifyCommand)
            Log.d(TAG, "selectCommand       = ${byteArray2HexString(mRetryCountVerifyCommand)}")
            Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
            if (commandResult[0] == 0x63.toByte()) {
                val retryCountHex = (commandResult.last().toInt() - 0xc0) and 0xff
                Log.d(TAG, " - retryCountHex = ${retryCountHex}")
            }
            // val pin = listOf(0, 5, 2, 3)
            // val pinEncoded = pin.map { char2Jisx0201(it.toString()[0]) }
            commandResult = isoDep.transceive(mPinVerifyCommand)
            Log.d(TAG, "selectCommand       = ${byteArray2HexString(mPinVerifyCommand)}")
            Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
//            if (commandResult[0] == 0x90.toByte()) {
//
//            }
            commandResult = isoDep.transceive(mSelectCommandDF1)
            Log.d(TAG, "selectCommand       = ${byteArray2HexString(mSelectCommandDF1)}")
            Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
            commandResult = isoDep.transceive(mReadBinaryCommandDF1EF01)
            Log.d(TAG, "selectCommand       = ${byteArray2HexString(mReadBinaryCommandDF1EF01)}")
            Log.d(TAG, "selectCommandResult = ${byteArray2HexString(commandResult)}")
            if (commandResult[commandResult.size-2] == 0x90.toByte()) {
                var currentPos = 0
                var pos = 0
                var dataList = mutableListOf<ByteArray>()
                repeat(17) {
                    val (pos, data) = getValueField(commandResult, currentPos)
                    currentPos = pos
                    dataList.add(data)
                }
                // jis x 0208 制定年番号
                val jisX0208 = "%02x".format(dataList[0].last())
                val name = byteArray2Jisx0208(dataList[1])
                val yomi = byteArray2Jisx0208(dataList[2])
                val tsusyo = byteArray2Jisx0208(dataList[3])
                val touitsu = byteArray2Jisx0208(dataList[4])
                val birthday = byteArray2Jisx0201Date(dataList[5])
                val location = byteArray2Jisx0208(dataList[6])
                val registeredAt = byteArray2Jisx0201Date(dataList[7])
                val refNumber = byteArray2Jisx0201(dataList[8])
                val color = byteArray2Jisx0208(dataList[9])
                val expiry = byteArray2Jisx0201Date(dataList[10])
                val require1 = byteArray2Jisx0208(dataList[11])
                val require2 = byteArray2Jisx0208(dataList[12])
                val require3 = byteArray2Jisx0208(dataList[13])
                val require4 = byteArray2Jisx0208(dataList[14])
                val commission = byteArray2Jisx0208(dataList[15])
                val cardNumber = byteArray2Jisx0201(dataList[16])

                Log.d(TAG, " - jisX0208     = ${jisX0208}")
                Log.d(TAG, " - name         = ${name}")
                Log.d(TAG, " - yomi         = ${yomi}")
                Log.d(TAG, " - tsusyo       = ${tsusyo}")
                Log.d(TAG, " - touitsu      = ${touitsu}")
                Log.d(TAG, " - birthday     = ${birthday}")
                Log.d(TAG, " - location     = ${location}")
                Log.d(TAG, " - registeredAt = ${registeredAt}")
                Log.d(TAG, " - refNumber    = ${refNumber}")
                Log.d(TAG, " - color        = ${color}")
                Log.d(TAG, " - expiry       = ${expiry}")
                Log.d(TAG, " - require1     = ${require1}")
                Log.d(TAG, " - require2     = ${require2}")
                Log.d(TAG, " - require3     = ${require3}")
                Log.d(TAG, " - require4     = ${require4}")
                Log.d(TAG, " - commission   = ${commission}")
                Log.d(TAG, " - cardNumber   = ${cardNumber}")
            }

            // ここまで処理
            isoDep.close()
        }
    }

    // private fun ByteArray.toHexString() = this.joinToString { "%02x".format(it) }
    private fun byteArray2HexString(byteArray: ByteArray) : String {
        return byteArray.joinToString(separator = " ") { "%02x".format(it) }
    }
    // Number to JisX0201 for PIN
    private fun char2Jisx0201 (c: Char) : Byte {
        return when (c) {
            '0' -> 0x30
            '1' -> 0x31
            '2' -> 0x32
            '3' -> 0x33
            '4' -> 0x34
            '5' -> 0x35
            '6' -> 0x36
            '7' -> 0x37
            '8' -> 0x38
            '9' -> 0x39
            else -> 0x00
        }.toByte()
    }
    // jis x 0208 変換
    private fun byteArray2Jisx0208(byteArray: ByteArray): String {
        val escape = byteArrayOf(0x1B.toByte(), 0x24.toByte(), 0x42.toByte())
        return String(escape + byteArray, charset("jis"))
    }
    // jis x 0201 変換
    private fun byteArray2Jisx0201(byteArray: ByteArray): String {
        val escape = byteArrayOf(0x1B.toByte(), 0x28.toByte(), 0x42.toByte())
        return String(escape + byteArray, charset("jis"))
    }
    // jis x 0201 変換
    private fun byteArray2Jisx0201Date(byteArray: ByteArray): String {
        val field = byteArray2Jisx0201(byteArray)
        val era = when (field[0]) {
            '1' -> "明治"
            '2' -> "大正"
            '3' -> "昭和"
            '4' -> "平成"
            else -> "令和"
        }
        val year = field.substring(1, 3)
        val month = field.substring(3, 5)
        val day = field.substring(5, 7)
        return "$era $year / $month / $day"
    }
    // jis x 0201 変換
    private fun getValueField(byteArray: ByteArray, pos: Int) : Pair<Int, ByteArray> {
        val length = byteArray[pos+1]
        return pos + 2 + length to byteArray.copyOfRange(pos + 2, pos + 2 + length)
    }

    // カレントディレクトリをMFへ
    val mSelectCommand = byteArrayOf(
        0x00.toByte(),   // CLA (class byte)
        0xA4.toByte(),   // INS (instraction byte)
        0x00.toByte(),   // P1  (param byte)
        0x00.toByte()    // P2  (param byte)
    )

    // カレントディレクトリを共通データ要素
    val mSelectCommandEF01 = byteArrayOf(
        0x00.toByte(),   // CLA
        0xA4.toByte(),   // INS
        0x02.toByte(),   // P1
        0x0C.toByte(),   // P2
        0x02.toByte(),   // Le
        0x2F.toByte(),   // data
        0x01.toByte()    // data
    )
    // 共通データを読み取る
    val mReadBinaryCommandEF01 = byteArrayOf(
        0x00.toByte(),   // CLA
        0xB0.toByte(),   // INS
        0x00.toByte(),   // P1
        0x00.toByte(),   // P2
        0x11.toByte(),   // Le
    )

    // カレントディレクトリをMFへ
    val mRetryCountVerifyCommand = byteArrayOf(
        0x00.toByte(),   // CLA (class byte)
        0x20.toByte(),   // INS (instraction byte)
        0x00.toByte(),   // P1  (param byte)
        0x81.toByte()    // P2  (param byte)
    )

    // 暗証番号を照合
    val mPinVerifyCommand = byteArrayOf(
        0x00.toByte(),   // CLA (class byte)
        0x20.toByte(),   // INS (instraction byte)
        0x00.toByte(),   // P1  (param byte)
        0x81.toByte(),   // P2  (param byte)
        0x04.toByte(),   // Lc
//        0x30.toByte(),
//        0x30.toByte(),
//        0x30.toByte(),
//        0x30.toByte()
    )

    // 暗証番号、記載事項を読みだす　DF1移動（DF1/EF01）
    val mSelectCommandDF1 = byteArrayOf(
        0x00.toByte(),   // CLA (class byte)
        0xa4.toByte(),   // INS (instraction byte)
        0x04.toByte(),   // P1  (param byte)
        0x0c.toByte(),   // P2  (param byte)
        0x10.toByte(),   // Lc
        0xA0.toByte(),   // AID
        0x00.toByte(),
        0x00.toByte(),
        0x02.toByte(),
        0x31.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
    )
    // 暗証番号、記載事項を読みだす　EF01読む（DF1/EF01）
    val mReadBinaryCommandDF1EF01 = byteArrayOf(
        0x00.toByte(),   // CLA (class byte)
        0xb0.toByte(),   // INS (instraction byte)
        0x81.toByte(),   // P1  (param byte)
        0x00.toByte(),   // P2  (param byte)
        0x00.toByte(),   // Lc
        0x03.toByte(),   // Lc
        0x70.toByte(),   // Lc
    )

}