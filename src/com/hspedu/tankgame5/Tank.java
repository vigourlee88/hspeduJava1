package com.hspedu.tankgame5;


public class Tank {
	private int x;//坦克的横坐标
	private int y;//坦克的纵坐标
	private int direct;//这里默认为0，向上坦克方向 0上,1右,2下,3左
	private int speed = 1;//坦克的速度
	boolean isLive = true;
	
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	//修改坦克坐标
	//上右下左 移动方法
	public void moveUp() {
		y -= speed;
	}
	public void moveRight() {
		x += speed;
	}
	public void moveDown() {
		y += speed;
	}
	public void moveLeft() {
		x -= speed;
	}
	public Tank(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
}
