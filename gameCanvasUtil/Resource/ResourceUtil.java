package gameCanvasUtil.Resource;

import java.io.File;

public class ResourceUtil
{
    /**
     * 連番ファイルの最終番号を返す
     * 0番からファイルを調べていって、最後の連番ファイルIDを返す
     *
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.typeの、xxxの部分)
     * @param file_type_array 拡張子リスト
     * 例えば、{ ".gif", ".png" } が指定されたら、gif, png の順に存在をチェックする
     * @return
     */
    public static int getResourceLastID(String base_file_path, String[] file_type_array)
    {
        for(int i=0; i<1000; i++)
        {
            boolean ok = false;
            for(int j=0; j<file_type_array.length; j++)
            {
                File file = new File(base_file_path + i + file_type_array[j]);
                if (file.exists())
                {
                    ok = true;
                    break;
                }
            }
            if(!ok) return i;
        }
        return 1000;
    }
}
