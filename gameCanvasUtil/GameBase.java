package gameCanvasUtil;

import gameCanvasUtil.GameInterface;
import gameCanvasUtil.GameCanvas;

/**
 * gameInterfaceを継承したクラス。内部にgameCanvasオブジェクトを持つ
 */
public abstract class GameBase implements GameInterface
{
    /** gameCanvasへの参照 */
    protected GameCanvas gc = null;

    /**
     * コンストラクタ。ただ単に、gameCanvasオブジェクトへの参照をもらうだけ。
     */
    public GameBase()
    {
        gc = GameCanvas.getInstance();
    }

    /**
     * 終了処理。
     */
    public abstract void finalGame();

    /**
     * 初期化処理。
     */
    public abstract void initGame();

    /**
     * 更新処理。
     */
    public abstract void updateGame();

    /**
     * 描画処理。
     */
    public abstract void drawGame();
}
