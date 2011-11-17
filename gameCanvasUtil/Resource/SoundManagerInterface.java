package gameCanvasUtil.Resource;

/**
 * サウンドマネージャのインターフェース
 *
 * 現時点で、MIDI,WAV(オンメモリ),WAV(ストリーミング)の3つのサブクラスを持つ
 */
public interface SoundManagerInterface
{
    /**
     * 初期化。リソースをまとめてロードする
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.wavの、xxxの部分)
     */
    public void init(String base_file_path);

    /**
     * サウンドの更新処理
     */
    public void update();

    /**
     * サウンドを再生する
     *
     * @param id サウンドのIDを指定
     * @param loop ループを行うかをセット。行うならtrue。
     */
    public void playSound(int id, boolean loop);

    /**
     * サウンドの停止を行う
     *
     * @param id 停止する音のID
     */
    public void stopSound(int id);

    /**
     * 全てのサウンドの停止を行う
     */
    public void stopSound();

    /**
     * サウンドの一時停止を行う
     *
     * @param id 一時停止する音のID
     */
    public void pauseSound(int id);

    /**
     * 全てのサウンドの一時停止を行う
     */
    public void pauseSound();

    /**
     * サウンドの音量を変更する
     *
     * @param id どの音を変更するか？
     * @param vol 音の大きさを指定(0-100)
     */
    public void changeVolume(int id, int vol);

    /**
     * サウンドの音量を変更する
     *
     * @param vol 音の大きさを指定(0-100)
     */
    public void changeVolume(int vol);
}
