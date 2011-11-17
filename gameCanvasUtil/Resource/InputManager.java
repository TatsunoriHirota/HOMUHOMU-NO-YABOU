package gameCanvasUtil.Resource;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * キー入力、マウス入力の管理を行うクラス
 *
 * 主な仕事はイベントで来たものをバッファすること。
 *
 */
public class InputManager
{

    // キーの関連
    public static final int BUTTON_UP = 0;
    public static final int BUTTON_DOWN = 1;
    public static final int BUTTON_LEFT = 2;
    public static final int BUTTON_RIGHT = 3;

    public static final int BUTTON_A = 4;
    public static final int BUTTON_B = 5;
    public static final int BUTTON_C = 6;
    public static final int BUTTON_D = 7;

    public static final int BUTTON_PAUSE = 8;
    public static final int BUTTON_L = 9;
    public static final int BUTTON_R = 10;
    public static final int BUTTON_SELECT = 11;

    private static final int KEYLIST[] = { KeyEvent.VK_UP, KeyEvent.VK_DOWN,
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_Z, KeyEvent.VK_X,
            KeyEvent.VK_C, KeyEvent.VK_V, KeyEvent.VK_ENTER, KeyEvent.VK_A,
            KeyEvent.VK_S, KeyEvent.VK_SPACE, };

    // キー入力情報のバッファリングの為に使用
    private int Push_Key_State[] = null;
    private boolean keyReleaseFlag[] = null;
    private boolean keyPushFlag[] = null;

    // マウスの座標
    private int mouse_x, mouse_y;
    // マウスのクリック時間
    private int mouse_click_time;

    // シングルトン用オブジェクト
    private static InputManager instance = null;

    /**
     * @return オブジェクトを返す
     */
    public static InputManager getInstance()
    {
        if (instance == null)
            instance = new InputManager();
        return instance;
    }

    /**
     * プライベートコンストラクタ
     */
    private InputManager()
    {
        this.Push_Key_State = new int[KEYLIST.length];
        this.keyReleaseFlag = new boolean[KEYLIST.length];
        this.keyPushFlag = new boolean[KEYLIST.length];
        this.mouse_click_time = 0;
        this.mouse_x = 0;
        this.mouse_y = 0;
    }

    /**
     * マウスが入ったときに呼ばれるメソッド
     *
     * @param e マウスイベントオブジェクト
     */
    public void mouseEntered(MouseEvent e)
    {
        this.mouse_x = e.getX();
        this.mouse_y = e.getY();
    }

    /**
     * マウスが出たときに呼ばれるメソッド
     *
     * @param e マウスイベントオブジェクト
     */
    public void mouseExited(MouseEvent e)
    {
        this.mouse_x = e.getX();
        this.mouse_y = e.getY();
    }

    /**
     * マウスがクリックされたに呼ばれるメソッド
     *
     * @param e マウスイベントオブジェクト
     */
    public void mouseClicked(MouseEvent e)
    {
        this.mouse_x = e.getX();
        this.mouse_y = e.getY();

    }

    /**
     * マウスが押されたときに呼ばれるメソッド
     *
     * @param e マウスイベントオブジェクト
     */
    public void mousePressed(MouseEvent e)
    {
        this.mouse_x = e.getX();
        this.mouse_y = e.getY();
        mouse_click_time = 1;
    }

    /**
     * マウスが離されたときに呼ばれるメソッド
     *
     * @param e マウスイベントオブジェクト
     */
    public void mouseReleased(MouseEvent e)
    {
        this.mouse_x = e.getX();
        this.mouse_y = e.getY();
        mouse_click_time = -1;
    }

    /**
     * マウスがドラッグされたときに呼ばれるメソッド
     *
     * @param e マウスイベントオブジェクト
     */
    public void mouseDragged(MouseEvent e)
    {
        this.mouse_x = e.getX();
        this.mouse_y = e.getY();
    }

    /**
     * マウスが動いたときに呼ばれるメソッド
     *
     * @param e マウスイベントオブジェクト
     */
    public void mouseMoved(MouseEvent e)
    {
        this.mouse_x = e.getX();
        this.mouse_y = e.getY();

    }

    /**
     * マウスのX座標を調べる
     *
     * @return マウスのX座標を返す
     */
    public int getMouseX()
    {
        return this.mouse_x;
    }

    /**
     * マウスのY座標を調べる
     *
     * @return マウスのY座標を返す
     */
    public int getMouseY()
    {
        return this.mouse_y;
    }

    /**
     * マウスのボタンを押している時間を調べる
     *
     * @return マウスの押している時間を調べる
     */
    public int getMouseClickLength()
    {
        return this.mouse_click_time;
    }

    /**
     * マウスのボタンを押した瞬間か調べる
     *
     * @return マウスのボタンを押した瞬間ならtrueを返す
     */
    public boolean isMousePushed()
    {
        return this.mouse_click_time == 1;
    }

    /**
     * マウスのボタンを離した瞬間かを調べる
     *
     * @return マウスのボタンを離した瞬間ならtrueを返す
     */
    public boolean isMouseReleased()
    {
        return this.mouse_click_time == -1;
    }

    /**
     * マウスのボタンを押しているか調べる
     *
     * @return マウスのボタンを押していたらtrueを返す
     */
    public boolean isMousePress()
    {
        return this.mouse_click_time >= 1;
    }

    /**
     * マウスやキーの状態を更新する
     */
    public void updateKeyData()
    {
        for (int i = 0; i < KEYLIST.length; i++)
        {
            if (Push_Key_State[i] != 0)
            {
                Push_Key_State[i] += 1;
            }
        }
        for (int i = 0; i < KEYLIST.length; i++)
        {
            if (this.keyPushFlag[i] && this.getKeyState(i) == 0)
            {
                this.Push_Key_State[i] = 1;
            }
            else if (this.keyReleaseFlag[i] && this.getKeyState(i) > 0)
            {
                this.Push_Key_State[i] = -1;
                this.keyReleaseFlag[i] = false;
            }
            this.keyPushFlag[i] = false;
        }

        if (mouse_click_time != 0)
            mouse_click_time++;
    }

    /**
     * キーが押されたときに呼び出されるメソッド
     *
     * @param e キーイベント
     */
    public synchronized void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();
        for (int i = 0; i < KEYLIST.length; i++)
        {
            if (KEYLIST[i] == key)
            {
                this.keyPushFlag[i] = true;
                break;
            }
        }
    }

    /**
     * キーが離されたときに呼び出されるメソッド
     *
     * @param e キーイベント
     */
    public synchronized void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();
        for (int i = 0; i < KEYLIST.length; i++)
        {
            if (KEYLIST[i] == key)
            {
                this.keyReleaseFlag[i] = true;
                break;
            }
        }
    }

    /**
     * キーがタイプされたときらしい
     *
     * @param e キーイベント
     */
    public void keyTyped(KeyEvent e)
    {
    }

    /**
     * キーの状態を得る
     *
     * @param num キーコード
     * @return キーの押している長さを返す
     */
    private int getKeyState(int num)
    {
        if (num < 0 || num >= KEYLIST.length)
        {
            return -1;
        }
        return Push_Key_State[num];
    }

    /**
     * キーを押している長さを返す
     *
     * @param number 調べるキーの番号を指定する
     * @return 調べているキーの長さを返す
     */
    public int getKeyPressLength(int number)
    {
        return getKeyState(number);
    }

    /**
     * キーが押しているかどうかを返す
     *
     * @param key 調べたいキーを指定
     * @return trueならば、調べているキーが押されていて、falseなら押されていない
     */
    public boolean isKeyPressed(int key)
    {
        return (this.getKeyState(key) > 0);
    }

    /**
     * キーが押された瞬間かを返す
     *
     * @param key 調べたいキーを指定
     * @return trueならば、その指定キーが押された瞬間。falseならば、押された瞬間ではない．
     */
    public boolean isKeyPushed(int key)
    {
        return (this.getKeyState(key) == 1);
    }

    /**
     * キーが離された瞬間かを返す
     *
     * @param key 調べたいキーを指定
     * @return trueならば、その指定キーが離された瞬間。falseならば、離された瞬間ではない．
     */
    public boolean isKeyReleased(int key)
    {
        return (this.getKeyState(key) < 0);
    }
}
