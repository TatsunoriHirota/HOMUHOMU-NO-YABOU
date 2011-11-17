package gameCanvasUtil.Collision;

/**
 * 2D用のベクトルクラスです。
 */
public class MyVector2D
{
    // ベクトルのＸ成分、Ｙ成分
    private double x, y;

    /**
     * コンストラクタ
     */
    public MyVector2D()
    {
        this.x = 0.0;
        this.y = 0.0;
    }

    /**
     * コンストラクタ
     *
     * @param vec 引数vecの中身をコピーします
     */
    public MyVector2D(MyVector2D vec)
    {
        this.x = vec.x;
        this.y = vec.y;
    }

    /**
     * コンストラクタ
     *
     * @param _x Ｘ成分の指定
     * @param _y Ｙ成分の指定
     */
    public MyVector2D(double _x, double _y)
    {
        this.x = _x;
        this.y = _y;
    }

    /**
     * X成分の取得
     *
     * @return X成分
     */
    public double getX()
    {
        return this.x;
    }

    /**
     * Ｙ成分の取得
     *
     * @return Ｙ成分
     */
    public double getY()
    {
        return this.y;
    }

    /**
     * Ｘ成分の指定
     *
     * @param _x Ｘ成分の指定
     */
    public void setX(double _x)
    {
        this.x = _x;
    }

    /**
     * Ｙ成分の指定
     *
     * @param _y Ｙ成分の指定
     */
    public void setY(double _y)
    {
        this.y = _y;
    }

}
