package gameCanvasUtil.Resource;

import javax.sound.sampled.*;

/**
 * オンメモリwavのサウンドマネージャ
 *
 * 連番のサウンドファイルをオンメモリに読み込んで管理を行う
 */
public class SoundManager implements SoundManagerInterface
{
    // リソース数(配列の要素数)
    private int resourceNum;

    // サウンドを格納するオブジェクト
    private Clip audio[] = null;

    // シングルトンインスタンス
    private static SoundManager instance = null;

    // プライベートコンストラクタ
    private void SoundManager()
    {
        resourceNum = 0;
    }

    /**
     * @return オブジェクトを返す
     */
    public static SoundManager getInstance()
    {
        if (instance == null)
            instance = new SoundManager();
        return instance;
    }

    /**
     * 初期化。リソースをまとめてロードする
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.wavの、xxxの部分)
     */
    public void init(String base_file_path)
    {
        this.loadNumberingSound(base_file_path);
    }

    /**
     * 連番のwavファイルを、ファイルが存在しなくなるまで読む
     *
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.wavの、xxxの部分)
     */
    private void loadNumberingSound(String base_file_path)
    {
        String type_list[] = { ".wav" };
        resourceNum = ResourceUtil.getResourceLastID(base_file_path, type_list);
        if(resourceNum <= 0) return;

        this.audio = new Clip[resourceNum];

        for (int i = 0; i < resourceNum; i++)
        {
            this.audio[i] = loadSound(base_file_path + i + ".wav");
            if (this.audio[i] == null)
                break;
        }
    }

    /**
     * ファイル名からクリップをロードする
     *
     * @param file_name ファイル名
     * @return 生成したクリップ
     */
    private Clip loadSound(String file_name)
    {
        Clip val = null;
        try
        {
            AudioInputStream audioInputStream = null;
            audioInputStream = AudioSystem.getAudioInputStream(this.getClass()
                    .getResourceAsStream("/" + file_name));

            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            val = (Clip) AudioSystem.getLine(info);
            val.open(audioInputStream);
            audioInputStream.close();
        }
        catch (Exception e)
        {
            System.out.println("loadSound Exception " + file_name + "::" + e);
            return null;
        }
        return val;
    }

    /**
     * サウンドを再生する
     *
     * @param id 再生する音のID
     * @param loop trueならループを行う
     */
    public void playSound(int id, boolean loop)
    {
        try
        {
            /* Mac環境で問題があったのでコメントアウト
            // 再生中なら停止する
            if (audio[id].isActive())
            {
                this.stopSound(id);
            }

            // 再生終了状態(現在位置が末尾である)なら先頭にシーク
            if (audio[id].getFrameLength() == audio[id].getFramePosition())
            {
                audio[id].setFramePosition(0);
            }
            */
            // 停止して
            this.stopSound(id);
            // 先頭にシーク
            audio[id].setFramePosition(0);

            if (loop)
            {
                // ループ再生を行う
                audio[id].loop(Clip.LOOP_CONTINUOUSLY);
            }
            else
            {
                // 単一回再生を行う
                audio[id].start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * サウンドを停止する
     *
     * @param id 停止するサウンドのID
     */
    public void stopSound(int id)
    {
        try
        {
            // 停止させて
            audio[id].stop();

            // 先頭にシークする
            audio[id].setFramePosition(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * サウンドを一時停止する
     *
     * @param id 一時停止するサウンドのID
     */
    public void pauseSound(int id)
    {
        try
        {
            audio[id].stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 全てのサウンドを一時停止する
     */
    public void pauseSound()
    {
        for (int i = 0; i < resourceNum; i++)
        {
            pauseSound(i);
        }
    }

    /**
     * 全てのサウンドを停止する
     */
    public void stopSound()
    {
        for (int i = 0; i < resourceNum; i++)
        {
            stopSound(i);
        }
    }

    /**
     * 全てのサウンドの音量変更
     */
    public void changeVolume(int vol)
    {
        for (int i = 0; i < resourceNum; i++)
        {
            changeVolume(i, vol);
        }
    }

    /**
     * サウンドの音量を変更する
     *
     * @param id 音量を変更するサウンドのID
     * @param vol 音の大きさを指定(0-100)
     */
    public void changeVolume(int id, int vol)
    {
        try
        {
            FloatControl control = (FloatControl) audio[id]
                    .getControl(FloatControl.Type.MASTER_GAIN);
            float range = control.getMaximum() - control.getMinimum();
            control.setValue(range * (float) Math.sqrt(vol / 100.0f)
                    + control.getMinimum());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * サウンドの更新処理(何もしない)
     */
    public void update()
    {
    }
}
