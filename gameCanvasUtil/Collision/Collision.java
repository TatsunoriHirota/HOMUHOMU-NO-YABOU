package gameCanvasUtil.Collision;

/**
 * 衝突判定などを取りまとめたクラスです
 */
public class Collision
{

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
    static public boolean checkHitRect(int x1, int y1, int w1, int h1, int x2,
            int y2, int w2, int h2)
    {
        if (x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2)
        {
            return true;
        }
        return false;
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
    static public boolean checkHitCircle(double x1, double y1, double r1,
            double x2, double y2, double r2)
    {
        double r = r1 + r2;
        double x = x1 - x2;
        double y = y1 - y2;

        if (x * x + y * y <= r * r)
        {
            return true;
        }
        return false;
    }

    /**
     * 二点間の距離を求める
     *
     * @param x1 点AのX座標
     * @param y1 点AのY座標
     * @param x2 点BのX座標
     * @param y2 点BのY座標
     * @return 点Aと点Bの距離を返す
     */
    static public double getPointDistance(double x1, double y1, double x2,
            double y2)
    {
        double x = x1 - x2;
        double y = y1 - y2;

        return Math.sqrt(x * x + y * y);
    }

    /**
     * 線分Aと線分Bの当たり判定
     *
     * @param st_x1 線分Aの始点X座標
     * @param st_y1 線分Aの始点Y座標
     * @param ed_x1 線分Aの終点X座標
     * @param ed_y1 線分Aの終点Y座標
     * @param st_x2 線分Bの始点X座標
     * @param st_y2 線分Bの始点Y座標
     * @param ed_x2 線分Bの終点X座標
     * @param ed_y2 線分Bの終点Y座標
     * @return 線分同士の交点を返す。(交点が無い場合、nullを返す)
     */
    static public MyVector2D checkHitVector(double st_x1, double st_y1,
            double ed_x1, double ed_y1, double st_x2, double st_y2,
            double ed_x2, double ed_y2)
    {

        MyVector2D p = null;
        double s, r;

        double acx = st_x2 - st_x1;
        double acy = st_y2 - st_y1;
        double bunbo = (ed_x1 - st_x1) * (ed_y2 - st_y2) - (ed_y1 - st_y1)
                * (ed_x2 - st_x2);

        if (bunbo == 0.0)
            return null;

        r = ((ed_y2 - st_y2) * acx - (ed_x2 - st_x2) * acy) / bunbo;
        s = ((ed_y1 - st_y1) * acx - (ed_x1 - st_x1) * acy) / bunbo;

        if (0.0 <= r && r <= 1.0 && 0.0 <= s && s <= 1.0)
        {
            p = new MyVector2D((int) (st_x1 + (ed_x1 - st_x1) * r),
                    (int) (st_y1 + (ed_y1 - st_y1) * r));
        }
        return p;
    }

    /**
     * 線分Aと線分Bの当たり判定を行う(new しない版)
     *
     * @param st_x1 線分Aの始点X座標
     * @param st_y1 線分Aの始点Y座標
     * @param ed_x1 線分Aの終点X座標
     * @param ed_y1 線分Aの終点Y座標
     * @param st_x2 線分Bの始点X座標
     * @param st_y2 線分Bの始点Y座標
     * @param ed_x2 線分Bの終点X座標
     * @param ed_y2 線分Bの終点Y座標
     * @param p 交差したポイントを格納する
     * @return 線分同士の交点があった場合、true,
     */
    static public boolean checkHitVector(double st_x1, double st_y1,
            double ed_x1, double ed_y1, double st_x2, double st_y2,
            double ed_x2, double ed_y2, MyVector2D p)
    {

        double s, r;

        double acx = st_x2 - st_x1;
        double acy = st_y2 - st_y1;
        double bunbo = (ed_x1 - st_x1) * (ed_y2 - st_y2) - (ed_y1 - st_y1)
                * (ed_x2 - st_x2);

        if (bunbo == 0.0)
            return false;

        r = ((ed_y2 - st_y2) * acx - (ed_x2 - st_x2) * acy) / bunbo;
        s = ((ed_y1 - st_y1) * acx - (ed_x1 - st_x1) * acy) / bunbo;

        if (0.0 <= r && r <= 1.0 && 0.0 <= s && s <= 1.0)
        {
            p.setX(st_x1 + (ed_x1 - st_x1) * r);
            p.setY(st_y1 + (ed_y1 - st_y1) * r);
        }
        return true;
    }

    /**
     * 線分と点の距離を得る
     *
     * @param px 点のX座標
     * @param py 点のY座標
     * @param st_x 線分の始点X座標
     * @param st_y 線分の始点Y座標
     * @param ed_x 線分の終点X座標
     * @param ed_y 線分の終点Y座標
     * @return 点と線分の距離
     */
    static public double getVectorPointDistance(double px, double py,
            double st_x, double st_y, double ed_x, double ed_y)
    {
        double distance = 0.0;

        double dx, dy;
        double a, b, t;
        double tx, ty;
        dx = ed_x - st_x;
        dy = ed_y - st_y;

        a = (dx * dx + dy * dy);
        b = dx * (st_x - px) + dy * (st_y - py);
        t = -b / a;

        if (t < 0.0)
            t = 0.0;
        if (t > 1.0)
            t = 1.0;

        tx = st_x + dx * t;
        ty = st_y + dy * t;

        distance = Math.sqrt((px - tx) * (px - tx) + (py - ty) * (py - ty));
        return distance;
    }

}
