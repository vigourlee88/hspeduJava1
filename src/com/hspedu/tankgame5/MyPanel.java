package com.hspedu.tankgame5;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JPanel;

import com.hspedu.tankgame5.Hero;

//为了监听 键盘事件 实现KeyListener
//为了让MyPanel不停的绘制子弹，需要MyPanel实现Runnable接口，当做一个线程使用
public class MyPanel extends JPanel implements KeyListener, Runnable {
	// 定义我的坦克
	Hero hero = null;
	// 定义敌人的坦克,放入到Vector集合中
	Vector<EnemyTank> enemyTanks = new Vector<>();
	//定义一个存放Node对象的Vector，用于恢复敌人坦克的坐标和方向
	Vector<Node> nodes = new Vector<>();
	//定义一个Vector,用于存放炸弹
	//说明，当子弹击中坦克时，就加入一个Bomb对象到bombs
	Vector<Bomb> bombs = new Vector<>();
	int enemyTankSize = 3;
	
	//定义三张炸弹图片，用于显示爆炸效果
	Image image1 = null;
	Image image2 = null;
	Image image3 = null;
	
	public MyPanel(String key) {
		nodes = Recorder.getNodesAndEnemyTankRec();
		//将MyPanel对象的 enemytanks设置给Recorder的enemyTanks
		Recorder.setEnemeyTanks(enemyTanks);
		hero = new Hero(500, 100);// 初始化自己的坦克
		hero.setSpeed(5);// 在创建坦克的时候，设置坦克的速度
		
		switch (key) {
			case "1":
				// 构造器中初始化敌人的坦克
				for (int i = 0; i < enemyTankSize; i++) {
					// 创建一个敌人的坦克
					EnemyTank enemyTank = new EnemyTank((100 * (i + 1)), 0);
					//将enemyTanks集合 设置给enemyTank对象!!!
					enemyTank.setEnemyTanks(enemyTanks);
					// 设置方向，向下
					enemyTank.setDirect(2);
					//启动敌人坦克线程，让他动起来
					new Thread(enemyTank).start();
					
					// 给该enemyTank 加入 颗子弹
					Shot shot = new Shot(enemyTank.getX() + 20, enemyTank.getY() + 60, enemyTank.getDirect());
					// 加入到enemyTank的Vector成员 shots是个Vector
					enemyTank.shots.add(shot);
					// 启动shot对象，它本身不是线程的,还要画敌人坦克的子弹在paint中
					new Thread(shot).start();
					// 加入
					enemyTanks.add(enemyTank);
				}
				break;
			case "2"://继续上局游戏
				// 构造器中初始化敌人的坦克
				for (int i = 0; i < nodes.size(); i++) {
					Node node = nodes.get(i);
					// 创建一个敌人的坦克
					EnemyTank enemyTank = new EnemyTank(node.getX(),node.getY());
					// 设置方向
					enemyTank.setDirect(node.getDirect());
					//将enemyTanks集合 设置给enemyTank对象!!!
					enemyTank.setEnemyTanks(enemyTanks);
					//启动敌人坦克线程，让他动起来
					new Thread(enemyTank).start();
					// 给该enemyTank 加入 颗子弹
					Shot shot = new Shot(enemyTank.getX() + 20, enemyTank.getY() + 60, enemyTank.getDirect());
					// 加入到enemyTank的Vector成员 shots是个Vector
					enemyTank.shots.add(shot);
					// 启动shot对象，它本身不是线程的,还要画敌人坦克的子弹在paint中
					new Thread(shot).start();
					// 加入
					enemyTanks.add(enemyTank);
				}
				break;
			default:
				System.out.println("你的输入有误...");
		}
		
		
		//初始化图片对象
		image1 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("bomb_1.gif"));
		image2 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("bomb_2.gif"));
		image3 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("bomb_3.gif"));
	}

	//编写方法，显示我方击毁地方坦克的信息
	public void showInfo(Graphics g) {
		
		//画出玩家的总成绩
		g.setColor(Color.BLACK);
		Font font = new Font("宋体",Font.BOLD,25);
		g.setFont(font);
		
		g.drawString("您累积击毁敌方坦克", 1020, 30);
		drawTank(1020,60,g,0,0);//画出一个敌方坦克，向上
		g.setColor(Color.BLACK);//这里 画笔的颜色要重置
	  //g.drawString("0",1080,100);
		g.drawString(Recorder.getAllEnemyTankNum() + "",1080,100);
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// 填充矩形边框，默认黑色
		g.fillRect(0, 0, 1000, 750);
		showInfo(g);


		if(hero != null && hero.isLive) {
			// 画出自己的坦克-封装方法
			// drawTank(hero.getX(), hero.getY(), g, 3, 1);//向右
			drawTank(hero.getX(), hero.getY(), g, hero.getDirect(), 1);// getDirect
		}
		// 画出hero射击的子弹
		if (hero.shot != null && hero.shot.isLive == true) {
			// System.out.println("子弹被绘制");
			g.draw3DRect(hero.shot.x, hero.shot.y, 1, 1, false);
		}
		//将hero的子弹集合shots,遍历取出绘制
//		for (int i = 0; i < hero.shots.size(); i++) {
//			Shot shot = hero.shots.get(i);
//			if (shot != null && shot.isLive ) {
//				g.draw3DRect(shot.x, shot.y, 1, 1, false);	
//			}else {//如果该shot对象已经无效,就从shots集合中拿掉
//				hero.shots.remove(shot);	
//			}
//		}
		
		//如果bombs 集合中有对象，就画出
		for (int i = 0; i < bombs.size(); i++) {
			//取出炸弹
			Bomb bomb = bombs.get(i);
			//根据当前这个bomb对象的life值去画出对应的图片
			if(bomb.life > 6) {
				g.drawImage(image1,bomb.x,bomb.y,60,60,this);
			}else if(bomb.life > 3) {
				g.drawImage(image2,bomb.x,bomb.y,60,60,this);
			}else {
				g.drawImage(image3,bomb.x,bomb.y,60,60,this);
			}
			//让这个炸弹的生命值减少
			bomb.lifeDown();
			//如果bomb life为0，就从bombs 的集合中删除
			if(bomb.life == 0) {
				bombs.remove(bomb);
			}
		}

		
		// 画出敌人的坦克，遍历Vector,必须使用enemyTanks.size(),有可能被打掉销毁
		for (int i = 0; i < enemyTanks.size(); i++) {
			// 从Vector 取出坦克
			EnemyTank enemyTank = enemyTanks.get(i);
			// 判断当前坦克是否还存活
			if (enemyTank.isLive) {//当敌人坦克是存活的，才画出该坦克
				drawTank(enemyTank.getX(), enemyTank.getY(), g, enemyTank.getDirect(), 0);// g 是画笔
				// 画出enemyTank 所有子弹
				for (int j = 0; j < enemyTank.shots.size(); j++) {
					// 取出当前对象的子弹
					Shot shot = enemyTank.shots.get(j);
					// 绘制子弹
					if (shot.isLive) {// isLive = true;
						g.draw3DRect(shot.x, shot.y, 1, 1, false);
					} else {
						// 从Vector中移除子弹
						enemyTank.shots.remove(shot);

					}
				}
			}
		}
	}

	// 编写方法，画出坦克
	/**
	 * 
	 * @param x      坦克的左上角x坐标
	 * @param y      坦克的左上角y坐标
	 * @param g      画笔
	 * @param direct 坦克方向(上下左右)
	 * @param type   坦克类型
	 */
	public void drawTank(int x, int y, Graphics g, int direct, int type) {

		// 根据不同类型的坦克，设置不同颜色
		switch (type) {
		case 0:// 我们的坦克
			g.setColor(Color.cyan);
			break;
		case 1:// 敌人的坦克
			g.setColor(Color.yellow);
			break;
		}

		// 根据坦克的方向，来绘制对应形状的坦克
		// direct 表方向(0:向上1:向右2:向下3:向左)
		switch (direct) {
		case 0:// 表向上
				// 绘制一个填充有当前颜色的3-D高亮矩形
			g.fill3DRect(x, y, 10, 60, false);// 画出坦克左边的轮子
			g.fill3DRect(x + 30, y, 10, 60, false);// 画出坦克右边的轮子
			g.fill3DRect(x + 10, y + 10, 20, 40, false);// 画出中间的坦克盖子
			g.fillOval(x + 10, y + 20, 20, 20);// 画出圆盖
			g.drawLine(x + 20, y + 30, x + 20, y);// 画出炮筒
			break;
		case 1:// 表向右

			g.fill3DRect(x, y, 60, 10, false);// 画出坦克上边边的轮子
			g.fill3DRect(x, y + 30, 60, 10, false);// 画出坦克下边的轮子
			g.fill3DRect(x + 10, y + 10, 40, 20, false);// 画出中间的坦克盖子
			g.fillOval(x + 20, y + 10, 20, 20);// 画出圆盖
			g.drawLine(x + 30, y + 20, x + 60, y + 20);// 画出炮筒
			break;
		case 2:// 表向下

			g.fill3DRect(x, y, 10, 60, false);// 画出坦克左边的轮子
			g.fill3DRect(x + 30, y, 10, 60, false);// 画出坦克右边的轮子
			g.fill3DRect(x + 10, y + 10, 20, 40, false);// 画出中间的坦克盖子
			g.fillOval(x + 10, y + 20, 20, 20);// 画出圆盖
			g.drawLine(x + 20, y + 30, x + 20, y + 60);// 画出炮筒
			break;
		case 3:// 表向左

			g.fill3DRect(x, y, 60, 10, false);// 画出坦克上边边的轮子
			g.fill3DRect(x, y + 30, 60, 10, false);// 画出坦克下边的轮子
			g.fill3DRect(x + 10, y + 10, 40, 20, false);// 画出中间的坦克盖子
			g.fillOval(x + 20, y + 10, 20, 20);// 画出圆盖
			g.drawLine(x + 30, y + 20, x, y + 20);// 画出炮筒
			break;
		default:
			System.out.println("暂时没有处理");
		}
	}
	//如果我们的坦克可以发射多个子弹
	//在判断我方子弹是否击中敌人坦克时，就需要把我们的子弹集合中
	//所有的子弹，都取出和敌人的所有坦克，进行判断
	public void hitEnemyTank() {
		
//		//遍历我们的子弹
//		for(int j = 0;j < hero.shots.size();j++) {
//			Shot shot = hero.shots.get(j);
//			// 判断是否击中了敌人坦克
//			if (shot != null && hero.shot.isLive) {// 当我的子弹还存活
//	
//				// 遍历敌人所有的坦克,Vector集合enemyTanks
//				for (int i = 0; i < enemyTanks.size(); i++) {
//					EnemyTank enemyTank = enemyTanks.get(i);
//					hitTank(hero.shot, enemyTank);// 击中的话
//				}
//			}
//		}
		//单颗子弹

		if (hero.shot != null && hero.shot.isLive) {// 当我的子弹还存活

			// 遍历敌人所有的坦克,Vector集合enemyTanks
			for (int i = 0; i < enemyTanks.size(); i++) {
				EnemyTank enemyTank = enemyTanks.get(i);
				hitTank(hero.shot, enemyTank);// 击中的话
			}
		}
		
	}
	
	//编写方法，判断敌人的坦克是否击中我的坦克
	public void hitHero() {
		//遍历所有的敌人坦克
		for (int i = 0; i < enemyTanks.size(); i++) {
			//取出敌人坦克
			EnemyTank enemyTank = enemyTanks.get(i);
			//遍历enemyTank对象的所有子弹
			for (int j = 0; j < enemyTank.shots.size(); j++) {
				//再取出子弹
				Shot shot = enemyTank.shots.get(j);
				//判断shot 是否击中我的坦克7
				if(hero.isLive && shot.isLive) {
					hitTank(shot,hero);
				}
				
			}
			
		}
	}

	// 编写方法，判断我方的子弹是否击中敌人坦克
	// 什么时候判断 我方的子弹是否击中敌人坦克? run方法
	public void hitTank(Shot s, Tank enemyTank) {
		// 判断s 击中坦克
		switch (enemyTank.getDirect()) {
		case 0:// 敌人坦克 向上
		case 2:// 敌人坦克 向下
			if (s.x > enemyTank.getX() && s.x < enemyTank.getX() + 40 && s.y > enemyTank.getY()
					&& s.y < enemyTank.getY() + 60) {
				s.isLive = false;
				enemyTank.isLive = false;
				//当我的子弹击中敌人的坦克后，将enemyTank 从Vector拿掉
				enemyTanks.remove(enemyTank);
				//当我方击毁一个敌人坦克时，就对数据allEnemyTankNum++
				//解读，EnemyTank可以是Hero，也可以是EnemyTank
				if(enemyTank instanceof EnemyTank) {
					Recorder.addAllEnemyTankNum();
				}
				//创建Bomb对象，加入到bombs集合
				//炸弹要跟着敌人坦克绑定
				Bomb bomb = new Bomb(enemyTank.getX(),enemyTank.getY());
				bombs.add(bomb);
			}
			break;
		case 1:// 敌人坦克 向右
		case 3:// 敌人坦克 向左
			if (s.x > enemyTank.getX() && s.x < enemyTank.getX() + 60 && s.y > enemyTank.getY()
					&& s.y < enemyTank.getY() + 40) {
				s.isLive = false;
				enemyTank.isLive = false;
				//当我的子弹击中敌人的坦克后，将enemyTank 从Vector拿掉
				enemyTanks.remove(enemyTank);
				//当我方击毁一个敌人坦克时，就对数据allEnemyTankNum++
				//解读，EnemyTank可以是Hero，也可以是EnemyTank
				if(enemyTank instanceof EnemyTank) {
					Recorder.addAllEnemyTankNum();
				}
				//炸弹要跟着敌人坦克绑定
				Bomb bomb = new Bomb(enemyTank.getX(),enemyTank.getY());
				bombs.add(bomb);
			}
			break;

		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	// 处理 wdsa 键按下的情况
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {// 按下w键，向上移动
			System.out.println("xx");
			// 改变坦克的方向
			hero.setDirect(0);
			// 修改坦克的坐标 y -=1;y是private,写到方法中
			// hero.setY(hero.getY()-1);
			if(hero.getY() > 0) {
				hero.moveUp();
			}	
			
		} else if (e.getKeyCode() == KeyEvent.VK_D) {// 按下d键，向右移动
			hero.setDirect(1);
			if((hero.getX() + 60) < 1000) {
				hero.moveRight();
			}
			
		} else if (e.getKeyCode() == KeyEvent.VK_S) {// 按下s键,向下移动
			hero.setDirect(2);
			if((hero.getY() + 60 )< 750) {
				hero.moveDown();
			}	

		} else if (e.getKeyCode() == KeyEvent.VK_A) {// 按下a键，向左移动
			hero.setDirect(3);
			if(hero.getX() > 0) {
					hero.moveLeft();
			}
		
		}

		// 如果用户按下的是J，就发射
		if (e.getKeyCode() == KeyEvent.VK_J) {
			//判断hero的子弹是否销毁, 发射一颗子弹的情况
			if(hero.shot == null || !hero.shot.isLive) {
				hero.shotEnemyTank();// Hero中射击，获取坦克的方向，位置创建不同的shot对象，开启shot.start(),shot是线程，会进入run()，改变坐标，输出坐标，自然退出
			}
			//发射多颗子弹
			//hero.shotEnemyTank();
			
		}
		// 让面板重绘
		this.repaint();

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void run() {// 每隔100毫秒，重绘区域，刷新绘图区域，子弹就移动起来
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 判断我们的子弹是否击中了敌人坦克
			hitEnemyTank();
			//判断敌人坦克是否击中我们
			hitHero();
			// 重绘
			this.repaint();
		}
	}
}
