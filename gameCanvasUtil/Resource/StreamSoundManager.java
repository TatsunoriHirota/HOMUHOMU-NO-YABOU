package gameCanvasUtil.Resource;

import javax.sound.sampled.*;

/**
 * サウンド(WAV)のストリーミング再生行っているクラス
 *
 * 同時に何個も音を鳴らすことも可能だが、かなり負荷が高い
 */
public class StreamSoundManager implements SoundManagerInterface
{
    // リソース数(配列の要素数)
    private int resourceNum;

    // 音を読み込むStream
    private AudioInputStream audioInputStream[] = null;
    // LINE
    private SourceDataLine line[] = null;
    // サウンドのループ
    private boolean loopFlag[] = null;

    // ラインバッファーに対する音を入れる
    private int bind[] = null;

    // 1ラインあたりのバッファ
    private static final int EXTERNAL_BUFFER_SIZE = 12800 / 3;
    // 同時再生できる数。負荷が高いので1を指定
    private static final int LINE_BUFFER_NUM = 1;

    // 音量を覚えておく
    private int volume[] = null;

    // 再生用バッファ
    private byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

    // ファイルのリストを保持します。
    private String fileNameList[] = null;

    // シングルトンインスタンス
    private static StreamSoundManager instance = null;

    // プライベートコンストラクタ
    private void StreamSoundManager()
    {
        resourceNum = 0;
    }

    /**
     * @return オブジェクトを返す
     */
    public static StreamSoundManager getInstance()
    {
        if (instance == null)
            instance = new StreamSoundManager();
        return instance;
    }

    /**
     * 初期化。リソースをまとめてロードする
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.wavの、xxxの部分)
     */
    public void init(String base_file_path)
    {
        String type_list[] = { ".wav" };
        resourceNum = ResourceUtil.getResourceLastID(base_file_path, type_list);
        if(resourceNum <= 0) return;

        this.fileNameList = new String[resourceNum];

        for (int i = 0; i < resourceNum; i++)
            fileNameList[i] = base_file_path + i + ".wav";

        // 必要なオブジェクトを生成
        this.audioInputStream = new AudioInputStream[LINE_BUFFER_NUM];
        line = new SourceDataLine[LINE_BUFFER_NUM];
        this.loopFlag = new boolean[LINE_BUFFER_NUM];
        this.bind = new int[LINE_BUFFER_NUM];
        this.volume = new int[LINE_BUFFER_NUM];
        for (int i = 0; i < LINE_BUFFER_NUM; i++)
            this.bind[i] = -1;
    }

    /**
     * サウンドを指定したラインにロードする
     *
     * @param line_id ラインの指定
     * @param snd_id サウンドのID指定
     */
    private void loadAndPlaySound(int line_id, int snd_id)
    {
        try
        {
            audioInputStream[line_id] = AudioSystem
                    .getAudioInputStream(this.getClass().getResourceAsStream(
                            "/" + fileNameList[snd_id]));

            AudioFormat audioFormat = audioInputStream[line_id].getFormat();

            // データラインの情報オブジェクトを生成します
            DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                    audioFormat);
            // 指定されたデータライン情報に一致するラインを取得します
            line[line_id] = (SourceDataLine) AudioSystem.getLine(info);
            // 指定されたオーディオ形式でラインを開きます
            line[line_id].open(audioFormat);
            // ラインでのデータ入出力を可能にします
            line[line_id].start();
            // 音量をセットします
            this.changeVolume(line_id, volume[line_id]);

            this.bind[line_id] = snd_id;
        }
        catch (Exception e)
        {
            System.out.println("soundStream play Error " + e);
            e.printStackTrace();
        }

    }

    /**
     * ストリーミング再生なので　ちょくちょく呼び出してやる
     *
     * 呼び出しが途切れると、再生も途切れるのです。
     */
    public void update()
    {
        int nBytesRead = 0;
        try
        {
            for (int i = 0; i < LINE_BUFFER_NUM; i++)
            {
                if (line[i] != null && line[i].isOpen())
                {
                    // オーディオストリームからデータを読み込みます
                    nBytesRead = audioInputStream[i].read(abData, 0,
                            abData.length);
                    if (nBytesRead >= 0)
                    {
                        // オーディオデータをミキサーに書き込みます
                        line[i].write(abData, 0, nBytesRead);
                    }
                    else
                    {
                        if (loopFlag[i])
                        {
                            this.loadAndPlaySound(i, this.bind[i]);
                        }
                        else
                        {
                            this.stopSound(i);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 開いているラインバッファーのIDを取得
     *
     * @return 開いているラインバッファーのIDを返す。-1の場合全て使用されています。
     */
    private int getFreeLineBuf()
    {
        try
        {
            for (int i = 0; i < LINE_BUFFER_NUM; i++)
            {
                if (this.line[i] == null || (!this.line[i].isOpen()))
                    return i;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 指定した音を指定したラインから鳴らす
     *
     * @param line_id ラインのID
     * @param snd_id 音のID指定
     * @param loop ループするかどうかを指定
     */
    private void playSound(int line_id, int snd_id, boolean loop)
    {
        try
        {
            this.loopFlag[line_id] = loop;
            if (this.line[line_id] != null && this.line[line_id].isOpen())
            {
                this.line[line_id].start();
            }
            else
            {
                this.loadAndPlaySound(line_id, snd_id);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 指定した音を鳴らす
     *
     * @param snd_id 音のID指定
     * @param loop ループするかどうかを指定
     */
    public void playSound(int snd_id, boolean loop)
    {
        int line_id = getFreeLineBuf();
        if (line_id >= 0)
        {
            this.playSound(line_id, snd_id, loop);
        }
        else
        {
            for (int i = 0; i < this.bind.length; i++)
            {
                if (bind[i] == snd_id)
                {
                    this.playSound(i, snd_id, loop);
                }
            }
        }
    }

    /**
     * このオブジェクトが管理している全てのサウンドバッファを停止させる
     */
    public void stopSound()
    {
        for (int i = 0; i < LINE_BUFFER_NUM; i++)
            this.stopSound(i);
    }

    /**
     * 指定したサウンドバッファの停止
     *
     * @param id 音IDの指定
     */
    public void stopSound(int id)
    {
        try
        {
            this.line[id].drain();
            this.line[id].close();
            this.line[id] = null;
            this.bind[id] = -1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 指定したサウンドバッファを一時停止
     *
     * @param id 音の指定
     */
    public void pauseSound(int id)
    {
        try
        {
            this.line[id].stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * サウンドバッファの音量を変更する
     *
     * @param id 音量を変更するサウンドバッファのID
     * @param param 音の大きさを指定(0-100)
     */
    public void changeVolume(int id, int param)
    {
        try
        {
            this.volume[id] = param;

            FloatControl control = (FloatControl) this.line[id]
                    .getControl(FloatControl.Type.MASTER_GAIN);
            float range = control.getMaximum() - control.getMinimum();
            control.setValue(range * (float) Math.sqrt(param / 100.0f)
                    + control.getMinimum());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 全てのサウンドバッファを一時停止する
     */
    public void pauseSound()
    {
        for (int i = 0; i < LINE_BUFFER_NUM; i++)
            this.pauseSound(i);
    }

    /**
     * 全てのサウンドバッファの音量変更
     */
    public void changeVolume(int vol)
    {
        for (int i = 0; i < LINE_BUFFER_NUM; i++)
            this.changeVolume(i, vol);
    }
}
