package gameCanvasUtil;

/**
 * gameのインターフェースクラスです。
 * gameCanvasに登録して、updateGame等のメソッド呼び出しを行ないます
 */
public interface GameInterface
{
    /** 更新処理 */
    public void updateGame();

    /** 描画処理 */
    public void drawGame();

    /** 初期化処理 */
    public void initGame();

    /** 終了処理 */
    public void finalGame();
}
