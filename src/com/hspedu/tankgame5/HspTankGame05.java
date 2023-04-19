package com.hspedu.tankgame5;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

import javax.swing.JFrame;

public class HspTankGame05 extends JFrame{

	//定义MyPanel
	MyPanel mp = null;
	static Scanner scanner = new Scanner(System.in);
	public static void main(String[] args) {
		
		HspTankGame05 hspTankGame01 = new HspTankGame05();
	}
	
	public HspTankGame05() {
		System.out.println("请输入选择 1: 新游戏 2: 继续上局");
		String key = scanner.next();
		mp = new MyPanel(key);
		//在创建MyPanel中启动线程
		//将mp放入到Thread,并启动
		Thread thread = new Thread(mp);//底层是静态代理模式
		thread.start();
		this.add(mp);//把画板(就是游戏的绘图区域)
		
		this.setSize(1300,950);
		this.addKeyListener(mp);//让JFrame监听mp键盘事件
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		//在JFrame中增加相应关闭窗口的处理
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			  //System.out.println("监听到关闭窗口了");
				Recorder.keepRecord();
				System.exit(0);
			}
		});
	}
}
