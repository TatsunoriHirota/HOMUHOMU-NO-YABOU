package gameCanvasUtil;

import java.awt.*; // グラフィックスクラスの読み込み
import javax.swing.JFrame;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;

import gameCanvasUtil.Collision.Collision;
import gameCanvasUtil.Resource.ImageManager;
import gameCanvasUtil.Resource.InputManager;
import gameCanvasUtil.Resource.MidiManager;
import gameCanvasUtil.Resource.SoundManager;
import gameCanvasUtil.Resource.SoundManagerInterface;

/**
 * @author kuro@ shift>>sega
 *         fujieda@ shift>>ntt
 *
 * さまざまな機能を取りまとめたクラス
 *
 * 基本的には他にたらい回すだけです
 */
public class GameCanvas
{
    /** 画面の幅 */
    public static final int WIDTH = 640;
    /** 画面の高さ */
    public static final int HEIGHT = 480;
    /** FPSの設定 */
    public static final int CONFIG_FPS = 30;

    /** 上ボタン */
    public static final int KEY_UP = InputManager.BUTTON_UP;
    /** 下ボタン */
    public static final int KEY_DOWN = InputManager.BUTTON_DOWN;
    /** 左ボタン */
    public static final int KEY_LEFT = InputManager.BUTTON_LEFT;
    /** 右ボタン */
    public static final int KEY_RIGHT = InputManager.BUTTON_RIGHT;

    /** Zキー */
    public static final int KEY_Z = InputManager.BUTTON_A;
    /** Xキー */
    public static final int KEY_X = InputManager.BUTTON_B;
    /** Cキー */
    public static final int KEY_C = InputManager.BUTTON_C;
    /** Vキー */
    public static final int KEY_V = InputManager.BUTTON_D;

    /** ENTER */
    public static final int KEY_ENTER = InputManager.BUTTON_PAUSE;

    /** SPACE */
    public static final int KEY_SPACE = InputManager.BUTTON_SELECT;

    // 色関係の定数

    /** 白色 */
    public static final int COLOR_WHITE = 0xFFFFFF;
    /** 黒色 */
    public static final int COLOR_BLACK = 0x000000;
    /** 灰色 */
    public static final int COLOR_GRAY = 0x808080;
    /** 赤色 */
    public static final int COLOR_RED = 0xFF0000;
    /** 青色 */
    public static final int COLOR_BLUE = 0x0000FF;
    /** 緑色 */
    public static final int COLOR_GREEN = 0x00FF00;
    /** 黄色 */
    public static final int COLOR_YELLOW = 0xFFFF00;
    /** 紫色 */
    public static final int COLOR_PURPLE = 0xFF00FF;
    /** シアン色 */
    public static final int COLOR_CYAN = 0x00FFFF;
    /** みずいろ */
    public static final int COLOR_AQUA = 0x7F7FFF;

    // 画像の管理とかをするオブジェクト
    private ImageManager imageManager;
    // SEの管理をするオブジェクト
    private SoundManagerInterface seManager;
    // BGMの管理をするオブジェクト
    private SoundManagerInterface bgmManager;
    // キーの入力を管理するオブジェクト
    private InputManager inputManager;
    // 実行するゲームインターフェースを継いだオブジェクト
    private GameInterface game;
    // セーブデータマネージャ
    private SavedataManager savedataManager;

    // ランダム用
    private Random rand = new Random();

    // 描画先
    private Graphics graphics;
    // オフスクリーン用のイメージ
    private Image offImage;

    // JFrameへの参照
    private JFrame frame;

    // シングルトン用に作成
    static private GameCanvas _gc;

    /**
     * シングルトン 既にgameCanvasオブジェクトが生成されていたら、それを返す。ないなら生成して返す。
     *
     * @return gameCanvasオブジェクトを返す
     */
    static public GameCanvas getInstance()
    {
        if (_gc == null)
            _gc = new GameCanvas();
        return _gc;
    }

    /**
     * シングルトンのため、プライベートコンストラクタ
     */
    private GameCanvas()
    {
    }

    /**
     * gameCanvasの初期化を行う
     */
    public void init(JFrame _f, GameInterface g)
    {
        this.frame = _f;

        this.setImageManager(ImageManager.getInstance());
        this.imageManager.init("res/img");

        this.setSEManager(SoundManager.getInstance());
        this.seManager.init("res/snd");
        this.changeSEVolume(100);

        this.setBGMManager(MidiManager.getInstance());
        this.bgmManager.init("res/bgm");
        this.changeBGMVolume(60);

        this.inputManager = InputManager.getInstance();
        this.savedataManager = SavedataManager.getInstance();
        this.readRecord();

        this.game = g;
        this.game.initGame();
    }

    /**
     * gameCanvasの終了時の処理です
     */
    public void finalize()
    {
        if (this.game != null)
        {
            this.game.finalGame();
        }
        // セーブ処理
        this.writeRecord();
    }

    /**
     * Graphicsクラスへの参照をセットする
     *
     * @param gr
     */
    public void setGraphics(Graphics gr, Image img)
    {
        this.graphics = gr;
        this.offImage = img;
        setFont("", 0, 25);
    }

    /**
     * 現在の画面を、画像として保存します
     *
     * @param file 拡張子を除いたファイル名を入れます
     * @return 保存に成功したかを返します
     */
    public boolean writeScreenImage(String file)
    {
        BufferedImage cur_off = new BufferedImage(GameCanvas.WIDTH,
                GameCanvas.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics gr = cur_off.getGraphics();
        gr.drawImage(offImage, 0, 0, null);
        try
        {
            return ImageIO.write(cur_off, "PNG", new File(file + ".png"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Windowの文字列を指定します
     *
     * @param title Windowのタイトルの文字列を指定
     */
    public void setWindowTitle(String title)
    {
        frame.setTitle(title);
    }

    /**
     * 文字列を描画する
     *
     * @param str 描画する文字列
     * @param x 左上のＸ座標
     * @param y 左上のＹ座標
     */
    public void drawString(String str, int x, int y)
    {
        graphics.drawString(str, x, y + graphics.getFont().getSize());
    }

    /**
     * 文字列を中心位置を指定して描画する
     *
     * @param str 描画する文字列
     * @param x 中心のＸ座標
     * @param y 上のＹ座標
     */
    public void drawCenterString(String str, int x, int y)
    {
        this.drawString(str, x - getStringWidth(str) / 2, y);
    }

    /**
     * 文字列を右寄せにして描画する
     *
     * @param str 描画する文字列
     * @param x 右上のＸ座標
     * @param y 上のＹ座標
     */
    public void drawRightString(String str, int x, int y)
    {
        this.drawString(str, x - getStringWidth(str), y);
    }

    // /フォントオブジェクト
    private Font myFont;
    private int FontSize;

    /**
     * drawStringなどで使用するフォントを変更します。
     *
     * @param font_name フォント名の指定
     * @param font_style フォントのスタイルの指定
     * @param font_size フォントのサイズの指定
     */
    public void setFont(String font_name, int font_style, int font_size)
    {
        myFont = new Font(font_name, font_style, font_size);
        graphics.setFont(myFont);
        FontSize = font_size;
    }

    /**
     * フォントサイズの変更
     *
     * @param font_size フォントのサイズを指定します
     */
    public void setFontSize(int font_size)
    {
        this.setFont("", 0, font_size);
    }

    /**
     * 文字列のドット幅を調べます
     *
     * 引数strをdrawString等で描画したときの幅を返します。
     *
     * @param str 調べる文字列
     * @return 引数strを描画したときの幅
     */
    public int getStringWidth(String str)
    {
        FontMetrics obj = graphics.getFontMetrics();
        return obj.stringWidth(str);
    }

    /**
     * drawStringや、drawRectなどで使用する色のセット
     *
     * @param color RGBで指定
     */
    public void setColor(int color)
    {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        setColor(r, g, b);
    }

    /**
     * drawStringや、drawRectなどで使用する色のセット
     *
     * @param red Ｒ成分
     * @param green Ｇ成分
     * @param blue Ｂ成分
     */
    public void setColor(int red, int green, int blue)
    {
        graphics.setColor(new Color(red, green, blue));
    }

    /**
     * 直線の描画
     *
     * @param sx 開始点のX座標
     * @param sy 開始店のY座標
     * @param ex 終了点のX座標
     * @param ey 終了点のY座標
     */
    public void drawLine(int sx, int sy, int ex, int ey)
    {
        graphics.drawLine(sx, sy, ex, ey);
    }

    /**
     * 中抜きの長方形の描画
     *
     * @param x 長方形の左上のＸ座標
     * @param y 長方形の左上のＹ座標
     * @param w 長方形の幅
     * @param h 長方形の高さ
     */
    public void drawRect(int x, int y, int w, int h)
    {
        graphics.drawRect(x, y, w, h);
    }

    /**
     * 塗りつぶしの長方形の描画
     *
     * @param x 長方形の左上のＸ座標
     * @param y 長方形の左上のＹ座標
     * @param w 長方形の幅
     * @param h 長方形の高さ
     */
    public void fillRect(int x, int y, int w, int h)
    {
        graphics.fillRect(x, y, w, h);
    }

    /**
     * 中抜き円の描画
     *
     * @param x 円の中心のＸ座標
     * @param y 円の中心のＹ座標
     * @param r 円の半径
     */
    public void drawCircle(int x, int y, int r)
    {
        graphics.drawArc(x - r, y - r, r * 2, r * 2, 0, 360);
    }

    /**
     * 塗りつぶし円の描画
     *
     * @param x 円の中心のＸ座標
     * @param y 円の中心のＹ座標
     * @param r 円の半径
     */
    public void fillCircle(int x, int y, int r)
    {
        graphics.fillArc(x - r, y - r, r * 2, r * 2, 0, 360);
    }

    /**
     * 画像描画
     *
     * @param id 画像のID。img0.gifならIDは0。img1.gifならIDは1。
     * @param x 画像の左上のＸ座標
     * @param y 画像の左上のＹ座標
     */
    public void drawImage(int id, int x, int y)
    {
        try
        {
            graphics.drawImage(imageManager.getImage(id), x, y, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 画像部分描画
     *
     * @param id 画像のID。img0.gifならIDは0。img1.gifならIDは1。
     * @param x 画像の左上のＸ座標
     * @param y 画像の左上のＹ座標
     * @param u 元画像より使用する部分の左上Ｘ座標
     * @param v 元画像より使用する部分の左上Ｙ座標
     * @param w 描画する幅
     * @param h 描画する高さ
     */
    public void drawClipImage(int id, int x, int y, int u, int v, int w, int h)
    {
        try
        {
            graphics.drawImage(imageManager.getImage(id), x, y, x + w, y + h, u, v, w + u, v
                    + h, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // / アフィントランス用
    private AffineTransform tr = new AffineTransform();

    /**
     * 画像を拡大縮小・回転をかけて表示
     *
     * @param id 画像のナンバーです
     * @param x 画像の中心Ｘ座標です
     * @param y 画像の中心Ｙ座標です
     * @param xsize 横にどのくらい拡大するか？100%を基準にしてます。(xsizeが100の時、等倍。200なら2倍の大きさ)
     * @param ysize 縦にどのくらい拡大するか？100%を基準にしてます。(ysizeが100の時、等倍。200なら2倍の大きさ)
     * @param angle 画像を何度回転させるか？(角度で指定)not弧度法
     */
    public void drawScaledRotateImage(int id, int x, int y, int xsize,
            int ysize, double rotate)
    {
        double w = (double) (this.getImageWidth(id)) / 2.0;
        double h = (double) (this.getImageHeight(id)) / 2.0;
        this.drawScaledRotateImage(id, x, y, xsize, ysize, rotate, w, h);
    }

    /**
     * 画像を拡大縮小・回転をかけて表示
     *
     * @param id　 画像のナンバーです
     * @param x 画像の中心Ｘ座標です
     * @param y 画像の中心Ｙ座標です
     * @param xsize 横にどのくらい拡大するか？100%を基準にしてます。(xsizeが100の時、等倍。200なら2倍の大きさ)
     * @param ysize 縦にどのくらい拡大するか？100%を基準にしてます。(ysizeが100の時、等倍。200なら2倍の大きさ)
     * @param angle 画像を何度回転させるか？(角度で指定)not弧度法
     * @param px 画像の回転の中心位置のＸ座標
     * @param py 画像の回転の中心位置のＹ座標
     */
    public void drawScaledRotateImage(int id, int x, int y, int xsize,
            int ysize, double rotate, double px, double py)
    {
        Image img = this.imageManager.getImage(id);
        if (img == null)
            return;
        Graphics2D gr = (Graphics2D) this.graphics;

        px *= xsize / 100.0;
        py *= ysize / 100.0;

        // 正規化
        tr.setToIdentity();

        tr.translate(x, y);
        tr.rotate(rotate * Math.PI / 360.0);
        tr.translate(-px, -py);

        tr.scale(xsize / 100.0, ysize / 100.0);
        gr.drawImage(img, tr, null);
    }

    /**
     * 画像の幅を得る
     *
     * @param id 画像のID。img0.gifならIDは0。img1.gifならIDは1。
     * @return 引数idで指定された画像の幅を返す
     */
    public int getImageWidth(int id)
    {
        return imageManager.getPicXsize(id);
    }

    /**
     * 画像の高さを得る
     *
     * @param id 画像のID。img0.gifならIDは0。img1.gifならIDは1。
     * @return 引数idで指定された画像の高さを返す
     */
    public int getImageHeight(int id)
    {
        return imageManager.getPicYsize(id);
    }

    /**
     * 乱数の種をセットする
     *
     * @param seed セットする乱数のシード
     */
    public void setSeed(int seed)
    {
        rand.setSeed(seed);
    }

    /**
     * min～maxまでの間のランダムな値を返す。
     *
     * @param min ランダムの最小値
     * @param max ランダムの最大値
     * @return 生成したランダム値
     */
    public int rand(int min, int max)
    {
        if(min < 0 || max < 0) return -1;

        int tmp = rand.nextInt();
        if (tmp < 0)
            tmp = -tmp;
        if (min < max)
        {
            max = max - min + 1;
            return tmp % max + min;
        }
        else
        {
            min = min - max + 1;
            return tmp % min + max;
        }
    }

    /**
     * ゲームステートをデフォルト状態にします
     */
    public void resetGame()
    {
        this.stopBGM();
        this.stopSE();
        this.graphics.translate(0, 0);
    }

    /**
     * 実行されるゲームクラスを再セット
     */
    public void resetGameInstance(GameInterface g)
    {
        if (this.game != null)
            this.game.finalGame();
        if (g != null)
            g.initGame();
        this.game = g;
    }

    /**
     * 更新時に行う処理
     */
    public void updateMessage()
    {
        if (this.game != null)
        {
            this.game.updateGame();
            this.writeRecord();
        }
        // BGMがストリーミング再生なのでアップデート
        this.bgmManager.update();
    }

    /**
     * 描画時に行う処理
     */
    public void drawMessage()
    {
        if (this.game != null && this.graphics != null)
            this.game.drawGame();
    }

    /**
     * 画面を白で塗りつぶす
     *
     */
    public void clearScreen()
    {
        setColor(255, 255, 255);
        fillRect(0, 0, WIDTH, HEIGHT);
    }

    /**
     * アプリを終了させる
     */
    public void exitApp()
    {

        this.game.finalGame();
        System.exit(0);
    }

    /**
     * YesNoを選択させるダイアログを出す
     *
     * @param message 確認用のメッセージ
     * @return 選択肢の結果を返す
     */
    public boolean showYesNoDialog(String message)
    {
        return (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                message, null, JOptionPane.YES_NO_OPTION));
    }

    /**
     * ユーザーに文字列の入力を求めるダイアログを出す
     *
     * @param message ユーザーに対するメッセージ
     * @param default_input デフォルト状態での入力
     * @return 入力されたメッセージを返す
     */
    public String showInputDialog(String message, String default_input)
    {
        return JOptionPane.showInputDialog(null, message, default_input);
    }










    // ---------------- Sound 関係 -------------------//

    // 現在再生中のBGM ID
    private int cur_bgm = -1;

    /**
     * BGMをループ再生する
     * BGMの再生は排他的で、2つ同時に再生は行わない
     *
     * @param id BGMの番号
     */
    public void playBGM(int id)
    {
        this.playBGM(id, true);
    }

    /**
     * BGMを再生する
     * BGMの再生は排他的で、2つ同時に再生は行わない
     *
     * @param id BGMの番号
     * @param loop ループするかどうかを指定する
     */
    public void playBGM(int id, boolean loop)
    {
        if (cur_bgm != id)
        {
            bgmManager.stopSound();
            bgmManager.playSound(id, loop);
        }
        cur_bgm = id;
    }

    /**
     * BGMの音量を変更する
     *
     * @param volume 音量の指定(0-100)
     */
    public void changeBGMVolume(int volume)
    {
        bgmManager.changeVolume(volume);
    }

    /**
     * BGMを停止する
     */
    public void stopBGM()
    {
        if (bgmManager != null)
            bgmManager.stopSound();

        cur_bgm = -1;
    }

    /**
     * BGMを一時停止する
     */
    public void pauseBGM()
    {
        bgmManager.pauseSound();
        cur_bgm = -1;
    }

    /**
     * SEを再生する
     *
     * @param id SEの番号
     */
    public void playSE(int id)
    {
        this.playSE(id, false);
    }

    /**
     * SEを再生する
     *
     * @param id SEの番号
     * @param loop SEをループさせるかセットする
     */
    public void playSE(int id, boolean loop)
    {
        this.seManager.playSound(id, loop);
    }

    /**
     * SEの音量を変更する
     *
     * @param volume 音量の指定(0-100)
     */
    public void changeSEVolume(int volume)
    {
        this.seManager.changeVolume(volume);
    }

    /**
     * 全てのSEを停止する
     */
    public void stopSE()
    {
        this.seManager.stopSound();
    }

    /**
     * 全てのSEを一時停止する
     */
    public void pauseSE()
    {
        this.seManager.pauseSound();
    }










    // -------------------キー入力関係-----------//

    /**
     * キーを押している長さを調べる
     * 引数で指定したキーが、どのくらい押されているかを返す
     *
     * @param keyCode KEY_0 なら「0キー」KEY_LEFT なら 「左カーソルキー」という感じで、調べたいキーを指定
     * @return 引数 keyCodeで指定されたキーを押している長さを返す。-1が離した直後。0なら押されていない。1以上は押している長さ
     */
    public int getKeyPressLength(int keyCode)
    {
        return inputManager.getKeyPressLength(keyCode);
    }

    /**
     * キーが押された状態かどうかを調べる
     * 引数で指定したキーが押されている場合、trueが返される
     *
     * @param keyCode KEY_0 なら「0キー」KEY_LEFT なら 「左カーソルキー」という感じで、調べたいキーを指定
     * @return 引数 keyCodeで指定されたキーが押された直後であった場合 true。離していた場合 false
     */
    public boolean isKeyPress(int keyCode)
    {
        return inputManager.isKeyPressed(keyCode);
    }

    /**
     * キーを押した瞬間かどうかを調べる
     * 引数で指定したキーが、押された直後であった場合trueを返す
     *
     * @param keyCode KEY_0 なら「0キー」KEY_LEFT なら 「左カーソルキー」という感じで、調べたいキーを指定
     * @return 引数 keyCodeで指定されたキーが押された直後であった場合 true。キーが押しっぱなし、離されている状態の場合 false
     */
    public boolean isKeyPushed(int keyCode)
    {
        return inputManager.isKeyPushed(keyCode);
    }

    /**
     * キーが離された瞬間かどうかを調べる
     * 引数で指定したキーが、離した直後であった場合trueを返す
     *
     * @param keyCode KEY_0 なら「0キー」KEY_LEFT なら 「左カーソルキー」という感じで、調べたいキーを指定
     * @return 引数 keyCodeで指定されたキーが離された直後であった場合 true。キーを押している、離しっぱなしの状態の場合 false
     */
    public boolean isKeyReleased(int keyCode)
    {
        return inputManager.isKeyReleased(keyCode);
    }

    /**
     * マウスのX座標を取得する
     *
     * @return 現在のマウスのX座標を返す
     */
    public int getMouseX()
    {
        return this.inputManager.getMouseX();
    }

    /**
     * マウスのY座標を取得する
     *
     * @return 現在のマウスのX座標を返す
     */
    public int getMouseY()
    {
        return this.inputManager.getMouseY();
    }

    /**
     * マウスのボタンが押されている時間を調べる
     *
     * @return マウスのボタンが押されている時間を調べる
     */
    public int getMouseClickLength()
    {
        return this.inputManager.getMouseClickLength();
    }

    /**
     * マウスのボタンを押した瞬間かどうかを調べる
     *
     * @return マウスのボタンを押した瞬間ならtrueを返す
     */
    public boolean isMousePushed()
    {
        return this.inputManager.isMousePushed();
    }

    /**
     * マウスのボタンを離した瞬間かどうかを調べる
     *
     * @return マウスのボタンを離した瞬間ならtrueを返す
     */
    public boolean isMouseReleased()
    {
        return this.inputManager.isMouseReleased();
    }

    /**
     * マウスのボタンが押された状態かどうかを調べる
     *
     * @return マウスのボタンを押していたらtrueを返す
     */
    public boolean isMousePress()
    {
        return this.inputManager.isMousePress();
    }



    // -------------------- セーブデータ系 ------------------//
    /**
     * セーブデータをファイルに書き出す
     */
    private void writeRecord()
    {
        this.savedataManager.writeRecord();
    }

    /**
     * ファイルからセーブデータを読み込む
     */
    private void readRecord()
    {
        this.savedataManager.readRecord();
    }

    /**
     * 読み込んだセーブデータからint値を読み出す
     *
     * @param idx データのインデックスを指定
     * @return 指定された位置からデータを読み出して返す
     */
    public int load(int idx)
    {
        return this.savedataManager.load(idx);
    }

    /**
     * セーブデータバッファにデータをセットする
     *
     * @param idx データのインデックスを指定
     * @param param セーブデータに書き込むint値
     */
    public void save(int idx, int param)
    {
        this.savedataManager.save(idx, param);
    }











    // --------------- 当たり判定系 ----------------//

    /**
     * 矩形Ａと矩形Ｂがぶつかっているか判定する
     *
     * @param x1 矩形Ａの左上Ｘ座標
     * @param y1 矩形Ａの左上Ｙ座標
     * @param w1 矩形Ａの幅
     * @param h1 矩形Ａの高さ
     * @param x2 矩形Ｂの左上Ｘ座標
     * @param y2 矩形Ｂの左上Ｙ座標
     * @param w2 矩形Ｂの幅
     * @param h2 矩形Ｂの高さ
     *
     */
    public boolean checkHitRect(int x1, int y1, int w1, int h1, int x2, int y2,
            int w2, int h2)
    {
        return Collision.checkHitRect(x1, y1, w1, h1, x2, y2, w2, h2);
    }

    /**
     * 画像Aと画像Bを指定位置に書いたとしたときに画像同士が当たっているかを確かめる
     *
     * drawImageでもし、画像Aと画像Bを書いたときに
     *
     * @param img1 画像AのIDを指定
     * @param x1 画像Aの左上X座標を指定
     * @param y1 画像Aの左上Y座標を指定
     * @param img2 画像BのIDを指定
     * @param x2 画像Bの左上X座標を指定
     * @param y2 画像Bの左上Y座標を指定
     * @return 画像同士が当たっているかどうかを返します
     */
    public boolean checkHitImage(int img1, int x1, int y1, int img2, int x2,
            int y2)
    {
        return this.checkHitRect(x1, y1, this.getImageWidth(img1),
                this.getImageHeight(img1), x2, y2, this.getImageWidth(img2),
                this.getImageHeight(img2));
    }

    /**
     * 円Aと円Bの当たり判定を行う
     *
     * @param x1 円Aの中心X座標
     * @param y1 円Aの中心Y座標
     * @param r1 円Aの半径
     * @param x2 円Bの中心X座標
     * @param y2 円Bの中心Y座標
     * @param r2 円Bの半径
     * @return 円Aと円Bが当たったかどうか？
     */
    public boolean checkHitCircle(int x1, int y1, int r1, int x2, int y2, int r2)
    {
        return Collision.checkHitCircle(x1, y1, r1, x2, y2, r2);
    }











    // ----------------- 数学系のメソッド ------------//
    /**
     * 平方根(√)を求める
     *
     * @param data 平方根を求めたい数字
     * @return 引数で与えられた数字の平方根を返す
     */
    public double sqrt(double data)
    {
        return Math.sqrt(data);
    }

    /**
     * cosを求める
     *
     * @param angle 角度を指定する(not 弧度法)
     * @return angleのcos
     */
    public double cos(double angle)
    {
        return Math.cos(angle * Math.PI / 180.0);
    }

    /**
     * sinを求める
     *
     * @param angle 角度を指定する(not 弧度法)
     * @return angleのsin
     */
    public double sin(double angle)
    {
        return Math.sin(angle * Math.PI / 180.0);
    }

    /**
     * atan2を求める(ベクトルの角度を求める)
     *
     * @param x ベクトルのX成分
     * @param y ベクトルのY成分
     * @return ベクトルの角度を返す
     */
    public double atan2(double x, double y)
    {
        return Math.atan2(x, y) * 180.0 / Math.PI;
    }











    // ------------- マネージャ関係 --------//

    /**
     * 画像のマネージャをセット
     *
     * @param manager マネージャ
     */
    private void setImageManager(ImageManager manager)
    {
        this.imageManager = manager;
    }

    /**
     * 効果音のマネージャをセット
     *
     * @param manager マネージャ
     */
    private void setSEManager(SoundManagerInterface manager)
    {
        if (this.seManager != null)
            this.seManager.stopSound();
        this.seManager = manager;
    }

    /**
     * BGMのマネージャをセット
     *
     * @param manager マネージャ
     */
    private void setBGMManager(SoundManagerInterface manager)
    {
        this.stopBGM();
        this.bgmManager = manager;
    }
}
