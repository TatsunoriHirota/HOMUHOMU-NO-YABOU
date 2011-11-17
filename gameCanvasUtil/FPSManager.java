package gameCanvasUtil;

/**
 * フレーム制御を行うためのクラスです。
 *
 * 主にFPSの管理をしています。 見る人が見たらアホな実装ですが、 System.currentTimeMillisの精度が悪いので仕方なく・・・
 */
public class FPSManager
{
    // 前の時間
    private long prevTime = 0;
    // 前一秒のFPS,FPS用のカウント，一フレームあたりの待ち時間
    private int fps, fpsTemp, waitTime;

    // 理想のFPS
    private int confFps = 60;

    // プライベートコンストラクタ
    private void FPSManager() {}

    // シングルトンインスタンス
    private static FPSManager instance = null;

    /**
     * @return オブジェクトを返す
     */
    public static FPSManager getInstance()
    {
        if (instance == null)
            instance = new FPSManager();
        return instance;
    }

    /**
     * 初期化
     *
     * @param conf_fps FPSの指定を行う
     */
    public void init(int conf_fps)
    {
        this.confFps = conf_fps;
        this.waitTime = 1000 / conf_fps;
    }

    /**
     * 待ち時間を算出してスリープ
     *
     * @return 実行中のFPS
     */
    public int Wait()
    {
        long time, tmp;

        time = System.currentTimeMillis();
        fpsTemp++;
        if (prevTime / 1000 != time / 1000)
        {
            fps = fpsTemp;
            fpsTemp = 0;
            if (fps > confFps + 1)
            {
                waitTime++;
            }
            else if (fps < confFps - 1)
            {
                waitTime--;
            }
            if (fps > confFps + confFps / 3)
            {
                waitTime = (fps - waitTime) / 3 + waitTime;
            }
        }
        tmp = time - prevTime;
        prevTime = time;
        if (tmp == 0)
        {
            tmp = 1;
        }
        this.sleep();
        return fps;
    }

    /**
     * スリープ処理を行う
     */
    private void sleep()
    {
        try
        {
            if (waitTime > 0)
                Thread.sleep(waitTime);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * FPSを調べる
     *
     * @return FPSを返す。
     */
    public int getFps()
    {
        return fps;
    }
}
