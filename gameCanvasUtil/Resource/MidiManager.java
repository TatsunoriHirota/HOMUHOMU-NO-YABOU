package gameCanvasUtil.Resource;

import java.io.*;

import javax.sound.midi.*;

/**
 * MIDIを管理し、再生などを行う
 *
 */
public class MidiManager implements SoundManagerInterface
{
    // リソース数(配列の要素数)
    private int resourceNum;

    // 音を鳴らすシーケンサー
    private Sequencer sequencer[] = null;
    private Synthesizer synthesizer = null;

    // 音ごとにループするかのフラグ
    private boolean loopFlag[] = null;

    // 音ごとの音量
    private int volume[] = null;

    // MIDIの仕様で、サウンド音量変更命令
    private static final int CHANGE_VOLUME = 7;

    // シングルトンインスタンス
    private static MidiManager instance = null;

    // プライベートコンストラクタ
    private void MidiManager()
    {
        resourceNum = 0;
    }

    /**
     * @return オブジェクトを返す
     */
    public static MidiManager getInstance()
    {
        if (instance == null)
            instance = new MidiManager();
        return instance;
    }

    /**
     * 初期化。リソースをまとめてロードする
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.midの、xxxの部分)
     */
    public void init(String base_file_path)
    {
        this.loadNumberingMidi(base_file_path);
        initSynthesizer();
    }

    /**
     * 連番のMIDIファイルをロードする
     *
     * @param base_file_path 読み込む連番ファイル名の指定(xxx0.midの、xxxの部分)
     */
    private void loadNumberingMidi(String base_file_path)
    {
        String type_list[] = { ".mid" };
        resourceNum = ResourceUtil.getResourceLastID(base_file_path, type_list);
        if(resourceNum <= 0) return;

        sequencer = new Sequencer[resourceNum];
        loopFlag = new boolean[resourceNum];
        volume = new int[resourceNum];

        for (int i = 0; i < resourceNum; i++)
        {
            this.sequencer[i] = loadMidi(base_file_path + i + ".mid");
            if (this.sequencer[i] == null)
                break;
        }
    }

    /**
     * ファイル名からMIDIをロードする
     *
     * @param file_name ファイル名
     * @return 読み込んだMIDIオブジェクトを返す
     */
    private Sequencer loadMidi(String file_name)
    {
        Sequencer tmp;
        try
        {
            tmp = MidiSystem.getSequencer();
            tmp.open();
            InputStream is = null;
            is = this.getClass().getResourceAsStream("/" + file_name);
            Sequence s = MidiSystem.getSequence(is);
            tmp.setSequence(s);
            is.close();
            return tmp;
        }
        catch (Exception e)
        {
            System.out.println("loadMidi Exception " + file_name + " " + e);
            return null;
        }
    }

    /**
     * MIDIを再生する
     *
     * @param id MIDIのIDを指定
     * @param flag ループを行うかをセット。行うならtrue。
     */
    public void playSound(int id, boolean flag)
    {
        try
        {
            this.loopFlag[id] = flag;
            sequencer[id].start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * MIDIを一時停止する
     *
     * @param id 停止させるMIDIを指定する
     */
    public void pauseSound(int id)
    {
        try
        {
            this.sequencer[id].stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * MIDIを停止させる
     *
     * @param id 停止させるMIDIを指定する
     */
    public void stopSound(int id)
    {
        try
        {
            this.sequencer[id].stop();
            this.sequencer[id].setTickPosition(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * MIDIの音量をセット
     *
     * @param id MIDIのID指定
     * @param vol 音量を指定(0-100)
     */
    public void changeVolume(int id, int vol)
    {
        try
        {
            if (vol < 0)
            {
                vol = 0;
            }
            else if (vol > 100)
            {
                vol = 100;
            }
            volume[id] = (vol * 127) / 100;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * MIDIの音量をセット
     *
     * @param id MIDIのID指定
     * @param vol 音量を指定(0-100)
     */
    private void setVolume(int id, int vol)
    {
        int midiVolume = vol;
        try
        {
            if (synthesizer.getDefaultSoundbank() == null)
            {
                // HARDWARE SYNTHESIZER
                try
                {
                    ShortMessage volumeMessage = new ShortMessage();
                    for (int i = 0; i < 16; i++)
                    {
                        volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE,
                                i, CHANGE_VOLUME, midiVolume);
                        MidiSystem.getReceiver().send(volumeMessage, -1);
                    }
                }
                catch (InvalidMidiDataException imde)
                {
                    System.err.println("Invalid MIDI data.");
                    return;
                }
                catch (MidiUnavailableException mue)
                {
                    System.err.println("MIDI unavailable.");
                    return;
                }
            }
            else
            {
                // SOFTWARE SYNTHESIZER:
                MidiChannel[] channels = synthesizer.getChannels();
                for (int c = 0; channels != null && c < channels.length; c++)
                {
                    channels[c].controlChange(CHANGE_VOLUME, midiVolume);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * サウンドの更新処理
     */
    public void update()
    {
        for (int i = 0; i < resourceNum; i++)
        {
            try
            {
                setVolume(i, volume[i]);
                if (sequencer[i] != null
                        && sequencer[i].getTickLength() <= sequencer[i]
                                .getTickPosition())
                {
                    this.sequencer[i].stop();
                    this.sequencer[i].setTickPosition(0);
                    this.sequencer[i].start();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 全てのサウンドを一時停止する
     */
    public void pauseSound()
    {
        for (int i = 0; i < resourceNum; i++)
        {
            pauseSound(i);
        }
    }

    /**
     * 全てのサウンドを停止する
     */
    public void stopSound()
    {
        for (int i = 0; i < resourceNum; i++)
        {
            stopSound(i);
        }
    }

    /**
     * 全てのサウンドの音量を変更する
     */
    public void changeVolume(int vol)
    {
        for (int i = 0; i < resourceNum; i++)
        {
            this.changeVolume(i, vol);
        }
    }

    /**
     * シンセサイザーの初期化
     */
    private void initSynthesizer()
    {
        if(resourceNum <= 0) return;

        try
        {
            if (!(sequencer[0] instanceof Synthesizer))
            {
                synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                if (synthesizer.getDefaultSoundbank() == null)
                {
                    // HARDWARE SYNTHESIZER
                    for (int i = 0; i < resourceNum; i++)
                    {
                        sequencer[i].getTransmitter().setReceiver(
                                MidiSystem.getReceiver());
                    }
                }
                else
                {
                    // SOFTWARE SYNTHESIZER
                    for (int i = 0; i < resourceNum; i++)
                    {
                        sequencer[i].getTransmitter().setReceiver(
                                synthesizer.getReceiver());
                    }
                }
            }
            else
            {
                synthesizer = (Synthesizer) sequencer[0];
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
