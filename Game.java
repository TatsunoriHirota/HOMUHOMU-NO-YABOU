//インポート
import gameCanvasUtil.*;

/** ゲームクラス。
 *
 * 学生が編集すべきソースコードです。
 */
public class Game extends GameBase
{
    /********* 変数定義はこちらに *********/
    int gameState;
    int time;
    int majo_x[]=new int[40];
    int majo_y[]=new int[40];
    int majo_kind[]=new int[40];
    boolean[] majo_alive_flag = new boolean[40];
    boolean[] majo_show_flag=new boolean[40];
    int Rnd;
    int str1;
    int str2;
    int card_now;
    int card_majo;
    int card_state;
    int j;
    int j_alt;
    int count;
    int homuSay;
    int homuFace;
    int picture;
    
    
    /********* 初期化の手順はこちらに *********/
    public void initGame()
    {
    	gameState=5;
    	time=1800;
	card_state=0;
	count=0;
	homuSay=gc.rand(1,3);
	homuFace=23;
	picture=gc.rand(29,35);

    	for(int i=0;i<40;i++){
		majo_x[i]=64*(i%10);
		majo_y[i]=90*(i/10);
    		majo_alive_flag[i]=true;
		majo_show_flag[i]=false;
		majo_kind[i]=(i+2)/2;
	}
	
	for(int i=0;i<40;i++){
		Rnd=gc.rand(0,39);
		str1=majo_kind[0];
		str2=majo_kind[Rnd];
		majo_kind[Rnd]=str1;
		majo_kind[0]=str2;
	}
    }

    /********* 物体の移動等の更新処理はこちらに *********/
    public void updateGame(){
    
	if(gameState==5){
		if(gc.isKeyPushed(gc.KEY_ENTER)){
			gameState=0;
		}
	}else if(gameState==0){
		if(gc.isKeyPushed(gc.KEY_ENTER)){
			gameState=1;
		}
	}else if(gameState==1){
		if(card_state==0){
			for(int i=0;i<40;i++){
				if((majo_alive_flag[i])&&(majo_show_flag[i]==false)){
					if(gc.isMousePushed()){
						if(gc.checkHitRect(majo_x[i],majo_y[i],64,90,gc.getMouseX(),gc.getMouseY(),1,1)){
							majo_show_flag[i]=true;
							card_majo=majo_kind[i];
							card_now=i;
							card_state=1;
						}
					}
				}
			}
		}else if(card_state==1){
			for(j=0;j<40;j++){
				if((majo_alive_flag[j])&&(majo_show_flag[j]==false)){
					if(gc.isMousePushed()){
						if(gc.checkHitRect(majo_x[j],majo_y[j],64,90,gc.getMouseX(),gc.getMouseY(),1,1)){
							if(majo_kind[j]==card_majo){
								majo_show_flag[j]=true;
								card_state=2;
								j_alt=j;
								homuSay=0;
							}else{
								majo_show_flag[j]=true;
								card_state=3;
								j_alt=j;
								homuSay=8;
							}
						}
					}
				}
			}
		}else if(card_state==2){
			if(gc.isMousePushed()){
				if(gc.checkHitRect(0,360,640,120,gc.getMouseX(),gc.getMouseY(),1,1)){
					majo_alive_flag[j_alt]=false;
					majo_alive_flag[card_now]=false;
					majo_show_flag[j_alt]=false;
					majo_show_flag[card_now]=false;
					card_state=0;
					time=time+150;
					count=count+1;
					homuSay=7;
					
					if(count==20){
						gameState=3;
						homuSay=6;
					}else if((count==15)){
						homuSay=5;
					}else if((count==10)){
						homuSay=4;
						homuFace=24;
					}
				}
			}
		}else if(card_state==3){
			if(gc.isMousePushed()){
				if(gc.checkHitRect(0,0,640,480,gc.getMouseX(),gc.getMouseY(),1,1)){
					majo_show_flag[j_alt]=false;
					majo_show_flag[card_now]=false;
					card_state=0;
					homuSay=gc.rand(1,3);
				}
			}
		}
	
		if(gc.isKeyPushed(gc.KEY_ENTER)){
			gameState=2;
		}

		time=time-1;
		if(time<0){
			gameState=4;
		}

	}else if(gameState==2){
		if(gc.isKeyPushed(gc.KEY_ENTER)){
			gameState=1;
		}
	}else if(gameState==3){
		if(gc.isKeyPushed(gc.KEY_ENTER)){
			initGame();
			gameState=0;
		}
	}else if(gameState==4){
		if(gc.isKeyPushed(gc.KEY_ENTER)){
			initGame();
			gameState=0;
		}
	}
    }
// gc.drawScaledRotateImage(画像id, int x, int y, 何％拡大縮小（X軸　１００％なら１００　）　,何％拡大縮小（Y軸　１００％なら１００　） , 0, 0, 0);
    /********* 画像の描画はこちらに *********/
    public void drawGame()
    {
	gc.clearScreen();
    
	if(gameState==5){
		gc.drawScaledRotateImage(27,0,0,100*640/gc.getImageWidth(27),100*480/gc.getImageHeight(27),0,0,0);
		gc.setColor(255,255,255);
		gc.setFontSize(40);
		gc.drawString("変態ほむらさんの野望",350,400);
		gc.drawString("Push ENTER to Start.",350,440);
	}else if(gameState==0){
		gc.drawImage(2,0,0);
		gc.setColor(255,255,255);
		gc.setFontSize(18);
		gc.drawString("暁美ほむら、君はどんな願いでソウルジェムを輝かせるんだい？？",80,400);
		gc.drawString("ENTERキーを押して、僕と契約して魔法少女になってよ!!",110,420);
	}else if(gameState==1){
		gc.drawScaledRotateImage(picture,0,0,100*640/gc.getImageWidth(picture),100*480/gc.getImageHeight(picture),0,0,0);
		gc.drawScaledRotateImage(homuFace,0,360,100*176/gc.getImageWidth(23),100*120/gc.getImageHeight(23),0,0,0);
		gc.drawScaledRotateImage(26,174,360,100*380/gc.getImageWidth(26),100*120/gc.getImageHeight(26),0,0,0);
		gc.drawScaledRotateImage(25,550,360,100*90/gc.getImageWidth(25),100*80/gc.getImageHeight(25),0,0,0);
		gc.setColor(0,0,0);
		gc.fillRect(550,440,90,40);
		gc.setColor(255,255,255);
		gc.drawString(time/30+"秒",580,450);

		switch(homuSay){
			case 0: gc.setColor(0,0,0); gc.setFontSize(18); gc.drawString("よし、やったわ！！",200,390); gc.drawString("右にあるソウルジェムをクリックして",200,410); gc.drawString("穢れをグリーフシードに吸い取らせて!!",200,430); break;
			case 1: gc.setColor(0,0,0); gc.drawString("ほむ？",200,410); break;
			case 2: gc.setColor(0,0,0); gc.drawString("まどか...私があなたを...",200,410); break;
			case 3: gc.setColor(0,0,0); gc.setFontSize(18); gc.drawString("こいつを仕留めるのは、わたし。",200,410); break;
			case 4: gc.setColor(0,0,0); gc.setFontSize(18); gc.drawString("まどかかわいいよまどかぁ(ハァハァ",200,410); break;
			case 5: gc.setColor(0,0,0); gc.setFontSize(18); gc.drawString("も、も、ももももうすぐ私の願いが...（フーッ、フーッ",200,410); break;
			case 6: gc.setColor(0,0,0); gc.setFontSize(18); gc.drawString("やったわ...ついに念願の...まどかぁぁあああ!!!",200,410); break;
			case 7: gc.setColor(0,0,0); gc.drawString("よし、邪魔者を排除したわ。",200,410); break;
			case 8: gc.setColor(0,0,0); gc.setFontSize(18); gc.drawString("ちっ、仕留め損ねたわ...",200,400); gc.drawString("適当な場所をクリックして再挑戦。",200,420); break;
		}

		for(int i=0;i<40;i++){
			if((majo_alive_flag[i])&&(majo_show_flag[i])){
				switch(majo_kind[i]){
					case 1: gc.drawScaledRotateImage(3,majo_x[i],majo_y[i],100*64/gc.getImageWidth(3),100*90/gc.getImageHeight(3), 0, 0, 0); break;
					case 2: gc.drawScaledRotateImage(4,majo_x[i],majo_y[i],100*64/gc.getImageWidth(4),100*90/gc.getImageHeight(4), 0, 0, 0); break;
					case 3: gc.drawScaledRotateImage(5,majo_x[i],majo_y[i],100*64/gc.getImageWidth(5),100*90/gc.getImageHeight(5), 0, 0, 0); break;
					case 4: gc.drawScaledRotateImage(6,majo_x[i],majo_y[i],100*64/gc.getImageWidth(6),100*90/gc.getImageHeight(6), 0, 0, 0); break;
					case 5: gc.drawScaledRotateImage(7,majo_x[i],majo_y[i],100*64/gc.getImageWidth(7),100*90/gc.getImageHeight(7), 0, 0, 0); break;
					case 6: gc.drawScaledRotateImage(8,majo_x[i],majo_y[i],100*64/gc.getImageWidth(8),100*90/gc.getImageHeight(8), 0, 0, 0); break;
					case 7: gc.drawScaledRotateImage(9,majo_x[i],majo_y[i],100*64/gc.getImageWidth(9),100*90/gc.getImageHeight(9), 0, 0, 0); break;
					case 8: gc.drawScaledRotateImage(10,majo_x[i],majo_y[i],100*64/gc.getImageWidth(10),100*90/gc.getImageHeight(10), 0, 0, 0); break;
					case 9: gc.drawScaledRotateImage(11,majo_x[i],majo_y[i],100*64/gc.getImageWidth(11),100*90/gc.getImageHeight(11), 0, 0, 0); break;
					case 10: gc.drawScaledRotateImage(12,majo_x[i],majo_y[i],100*64/gc.getImageWidth(12),100*90/gc.getImageHeight(12), 0, 0, 0); break;
					case 11: gc.drawScaledRotateImage(13,majo_x[i],majo_y[i],100*64/gc.getImageWidth(13),100*90/gc.getImageHeight(13), 0, 0, 0); break;
					case 12: gc.drawScaledRotateImage(14,majo_x[i],majo_y[i],100*64/gc.getImageWidth(14),100*90/gc.getImageHeight(14), 0, 0, 0); break;
					case 13: gc.drawScaledRotateImage(15,majo_x[i],majo_y[i],100*64/gc.getImageWidth(15),100*90/gc.getImageHeight(15), 0, 0, 0); break;
					case 14: gc.drawScaledRotateImage(16,majo_x[i],majo_y[i],100*64/gc.getImageWidth(16),100*90/gc.getImageHeight(16), 0, 0, 0); break;
					case 15: gc.drawScaledRotateImage(17,majo_x[i],majo_y[i],100*64/gc.getImageWidth(17),100*90/gc.getImageHeight(17), 0, 0, 0); break;
					case 16: gc.drawScaledRotateImage(18,majo_x[i],majo_y[i],100*64/gc.getImageWidth(18),100*90/gc.getImageHeight(18), 0, 0, 0); break;
					case 17: gc.drawScaledRotateImage(19,majo_x[i],majo_y[i],100*64/gc.getImageWidth(19),100*90/gc.getImageHeight(19), 0, 0, 0); break;
					case 18: gc.drawScaledRotateImage(20,majo_x[i],majo_y[i],100*64/gc.getImageWidth(20),100*90/gc.getImageHeight(20), 0, 0, 0); break;
					case 19: gc.drawScaledRotateImage(21,majo_x[i],majo_y[i],100*64/gc.getImageWidth(21),100*90/gc.getImageHeight(21), 0, 0, 0); break;
					case 20: gc.drawScaledRotateImage(22,majo_x[i],majo_y[i],100*64/gc.getImageWidth(22),100*90/gc.getImageHeight(22), 0, 0, 0); break;
				}
			}
		}
		
		for(int i=0;i<40;i++){
			if((majo_alive_flag[i])&&(majo_show_flag[i]==false)){
				gc.drawImage(1,majo_x[i],majo_y[i]);
			}
		}

	}else if(gameState==2){
		gc.setColor(0,0,0);
		gc.drawString("Pause",320,220);
	}else if(gameState==3){
		gc.drawScaledRotateImage(picture,0,0,100*640/gc.getImageWidth(picture),100*480/gc.getImageHeight(picture),0,0,0);
		gc.setColor(255,255,255);
		gc.drawString("Game Clear",500,430);
		gc.drawString("Push ENTER to continue.",500,450);
	}else if(gameState==4){
		gc.drawScaledRotateImage(28,0,0,100*640/gc.getImageWidth(28),100*480/gc.getImageHeight(28), 0, 0, 0);
		gc.setColor(255,255,255);
		gc.drawString("Game Over",500,430);
		gc.drawString("Push ENTER to restart.",500,450);
	}
    }

    /********* 終了時の処理はこちらに *********/
    public void finalGame() {}
}
