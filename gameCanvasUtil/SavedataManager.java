package gameCanvasUtil;

import java.io.*;

/**
 * セーブデータマネージャ
 *
 * int型配列のセーブデータをバイト列としてファイルに書き出す/読み込みを行う
 */
public class SavedataManager
{
    // セーブデータのサイズ
    private static final int SAVE_DATA_SIZE = 1024;

    // /セーブデータ用バッファ
    private int savedata[] = null;

    // セーブデータが書き換えられたかどうか
    private boolean savedataChanged = false;

    // プライベートコンストラクタ
    private void SavedataManager()
    {
        savedataChanged = false;
    }

    // シングルトンインスタンス
    private static SavedataManager instance = null;

    /**
     * @return オブジェクトを返す
     */
    public static SavedataManager getInstance()
    {
        if (instance == null)
            instance = new SavedataManager();
        return instance;
    }

    /**
     * セーブデータをファイルに書き出す
     */
    public void writeRecord()
    {
        if(savedataChanged)
        {
            byte tmp[] = intarr2bytearr(savedata);
            writeByteArray("savedata.dat", tmp);
            savedataChanged = false;
        }
    }

    /**
     * ファイルからセーブデータを読み込む
     */
    public void readRecord()
    {
        byte tmp[] = readByteArray("savedata.dat");
        if (tmp == null)
        {
            this.savedata = new int[SAVE_DATA_SIZE];
            this.writeRecord();
        }
        else
        {
            this.savedata = bytearr2intarr(tmp);
        }
    }

    /**
     * 読み込んだセーブデータからint値を読み出す
     *
     * @param idx データのインデックスを指定
     * @return 指定された位置からデータを読み出して返す
     */
    public int load(int idx)
    {
        return savedata[idx];
    }

    /**
     * セーブデータバッファにデータをセットする
     *
     * @param idx データのインデックスを指定
     * @param param セーブデータに書き込むint値
     */
    public void save(int idx, int param)
    {
        this.savedata[idx] = param;
        savedataChanged = true;
    }

    /**
     * 引数dataをファイルに書き込む
     *
     * @param file_name ファイル名
     * @param data データの指定
     * @return ファイル書き込みに成功したかどうか
     */
    public boolean writeByteArray(String file_name, byte data[])
    {
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(file_name);
            out.write(data);
            out.close();
        }
        catch (Exception e)
        {
            try
            {
                out.close();
            }
            catch (Exception e2)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * ファイルを読み込んで、バイト配列として返す
     *
     * @param file_name ファイル名
     * @return 読み込んだデータを返す
     */
    private byte[] readByteArray(String file_name)
    {
        byte[] data = null;
        ;
        File fp = new File(file_name);
        long size = fp.length();
        data = new byte[(int) size];

        if(readByteArray(file_name, data))
            return data;
        else
            return null;
    }

    /**
     * ファイルを読み込んで、引数のバイト配列に格納していく
     *
     * @param file_name ファイル名
     * @param data データの格納先
     * @return ファイル読み込みに成功すればtrueを返す
     */
    private boolean readByteArray(String file_name, byte data[])
    {
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(file_name);
            in.read(data);
            in.close();
        }
        catch (Exception e)
        {
            try
            {
                in.close();
            }
            catch (Exception e2)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * バイト配列の指定位置にint値を書き込む
     *
     * @param dest 配列を突っ込む先
     * @param offset 配列を突っ込むオフセット
     * @param src 突っ込む値
     */
    private void int2byte(byte dest[], int offset, int src)
    {
        dest[offset + 0] = (byte) ((src >> 0) & 0xff);
        dest[offset + 1] = (byte) ((src >> 8) & 0xff);
        dest[offset + 2] = (byte) ((src >> 16) & 0xff);
        dest[offset + 3] = (byte) ((src >> 24) & 0xff);
    }

    /**
     * バイト配列の指定位置からint値を抽出
     *
     * @param src 抜き出し元の配列
     * @param offset 抜き出すオフセット
     * @return 抽出したint値
     */
    private int byte2int(byte src[], int offset)
    {
        return ((src[offset + 0] & 0xff) << 0)
                + ((src[offset + 1] & 0xff) << 8)
                + ((src[offset + 2] & 0xff) << 16)
                + ((src[offset + 3] & 0xff) << 24);
    }

    /**
     * intの配列からbyteの配列へ変換します
     *
     * @param src byte配列に変換したいintの配列
     * @return 引数srcから変換したbyte配列
     */
    private byte[] intarr2bytearr(int src[])
    {
        byte val[] = new byte[src.length * 4];
        for (int i = 0; i < src.length; i++)
        {
            int2byte(val, i * 4, src[i]);
        }
        return val;
    }

    /**
     * byteの配列からintの配列へ変換します
     *
     * @param src int配列に変換したいbyteの配列
     * @return 引数byteから変換したint配列
     */
    private int[] bytearr2intarr(byte src[])
    {
        int val[] = new int[src.length / 4];
        for (int i = 0; i < val.length; i++)
        {
            val[i] = byte2int(src, i * 4);
        }
        return val;
    }
}
