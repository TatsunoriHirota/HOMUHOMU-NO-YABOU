package gameCanvasUtil.Resource;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.InputStream;

/**
 * 画像の管理を行うクラス
 *
 * 実際の描画は gameCanvasにImageオブジェクト返してやるから　やってくれー<br />
 * 画像は連番で読み込みます
 */
public class ImageManager
{
    // リソース数(配列の要素数)
    private int resourceNum;

    // 画像
    private Image img[] = null;

    // 画像のバッファサイズ。ここでは4MBを指定している
    private byte imageBuffer[] = new byte[1024 * 1024 * 4];

    // シングルトンインスタンス
    private static ImageManager instance = null;

    // プライベートコンストラクタ
    private void ImageManager()
    {
        resourceNum = 0;
    }

    /**
     * @return オブジェクトを返す
     */
    public static ImageManager getInstance()
    {
        if (instance == null)
            instance = new ImageManager();
        return instance;
    }

    /**
     * 初期化。リソースをまとめてロードする
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.wavの、xxxの部分)
     */
    public void init(String base_file_path)
    {
        this.loadNumberingImage(base_file_path);
    }

    /**
     * 連番の画像ファイルを、ファイルが存在しなくなるまで読む
     * ".gif", ".GIF", ".png", ".PNG", ".jpg", ".JPG" の順に存在をチェック
     *
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.pngの、xxxの部分)
     */
    private void loadNumberingImage(String base_file_path)
    {
        String type_list[] = { ".gif", ".GIF", ".png", ".PNG", ".jpg", ".JPG" };
        resourceNum = ResourceUtil.getResourceLastID(base_file_path, type_list);
        if(resourceNum <= 0) return;

        img = new Image[resourceNum];

        int i, j;
        for (i = 0; i < resourceNum; i++)
        {
            for (j = 0; j < type_list.length; j++)
            {
                File file = new File(base_file_path + i + type_list[j]);
                if (file.exists())
                {
                    this.img[i] = loadImage(base_file_path + i + type_list[j]);
                    if (this.img[i] != null)
                    {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 画像ファイルを読み込む。読み込めたらオブジェクトを返す
     *
     * @param file_name ファイル名
     * @return 読み込んだImageオブジェクトを返す
     */
    private Image loadImage(String file_name)
    {
        try
        {
            Image obj = null;
            int read = 0, tmp = 0;
            InputStream is = this.getClass().getResourceAsStream("/" + file_name);
            if (is == null)
            {
                return null;
            }
            while ((tmp = is.read(imageBuffer, read, imageBuffer.length
                    - read)) > 0)
                read += tmp;
            is.close();
            byte img_dat[] = new byte[read];
            System.arraycopy(imageBuffer, 0, img_dat, 0, read);
            obj = Toolkit.getDefaultToolkit().createImage(img_dat, 0, read);
            return obj;
        }
        catch (Exception e)
        {
            System.out.println("loadImage Exception " + file_name + "::" + e);
            return null;
        }
    }

    /**
     * 画像の幅を返す
     *
     * @param id 画像のID
     * @return 指定された画像の幅
     */
    public int getPicXsize(int id)
    {
        try
        {
            return img[id].getWidth(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 画像の高さを返す
     *
     * @param id 画像のID
     * @return 指定された画像の高さ
     */
    public int getPicYsize(int id)
    {
        try
        {
            return img[id].getHeight(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * ImageManager内で管理している画像を返す
     *
     * @param id 画像のID
     * @return 画像オブジェクト
     */
    public Image getImage(int id)
    {
        try
        {
            return img[id];
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
