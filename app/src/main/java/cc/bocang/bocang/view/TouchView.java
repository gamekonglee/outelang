package cc.bocang.bocang.view;


import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;

import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.ui.DiyActivity;

/**
 * 继承ImageView 实现了多点触碰的拖动和缩放
 *
 * @author Administrator
 *
 */
public class TouchView extends ImageView {
	static final int NONE = 0;
	static final int DRAG = 1; // 拖动中
	static final int ZOOM = 2; // 缩放中
	static final int BIGGER = 3; // 放大ing
	static final int SMALLER = 4; // 缩小ing
	private int mode = NONE; // 当前的事件
	PointF mid = new PointF();


	private float beforeLenght; // 两触点距离
	private float afterLenght; // 两触点距离
	private float scale = 0.025f; // 缩放的比例 X Y方向都是这个值 越大缩放的越快
	private float onDownZoomRotation;// 旋转角度
	private int screenW;
	private int screenH;
	float oldDist = 1f;
	/* 处理拖动 变量 */
	private int start_x;
	private int start_y;
	private int stop_x;
	private int stop_y;
	private TranslateAnimation trans; // 处理超出边界的动画


	private Context mContext;
	private GestureDetector mGestureDetector;// 手势监听变量
	private FrameLayout parentContainer;
	private FrameLayout sonContainer;
	private int mLightCount; // 该灯的编号
	public TouchView(Context context) {
		super(context);
		this.mContext = context;
		mGestureDetector = new GestureDetector(context, new MyGestureListener());// 注册手势监听器
		this.setPadding(0, 0, 0, 0);
	}

	// 把其容器对象传进来
	public void setContainer(FrameLayout parentContainer,
							 FrameLayout sunContainer) {
		this.parentContainer = parentContainer;
		this.sonContainer = sunContainer;
	}

	public void setmLightCount(int mLightCount) {
		this.mLightCount = mLightCount;
	}
	/**
	 * 就算两点间的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 处理触碰..
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (MotionEventCompat.getActionMasked(event)) {
			case MotionEvent.ACTION_DOWN:
				stop_x = (int) event.getRawX();
				stop_y = (int) event.getRawY();
				start_x = (int) event.getX();
				start_y = stop_y - this.getTop();

				mode = DRAG;
				if (event.getPointerCount() == 2)
					beforeLenght = spacing(event);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:

				if (spacing(event) > 10f) {
					mode = ZOOM;
					beforeLenght = spacing(event);
					onDownZoomRotation = rotation(event);
					midPoint(mid, event);
				}
				break;
			case MotionEvent.ACTION_UP:
			/* 判断是否超出范围 并处理 */
				int disX = 0;
				int disY = 0;
				viewPream = new FrameLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				viewPream.width = this.getWidth();
				viewPream.height = this.getHeight();
				viewPream.gravity = Gravity.TOP | Gravity.LEFT;
				viewPream.leftMargin = getLeft();
				viewPream.topMargin = getTop();
				setLayoutParams(viewPream);
				// 下面的代码是边界判断
				if (getHeight() <= screenH || this.getTop() < 0) {
					if (this.getTop() < 0) {
						int dis = getTop();
						this.layout(this.getLeft(), 0, this.getRight(),
								0 + this.getHeight());
						disY = dis - getTop();
					} else if (this.getBottom() > screenH) {
						disY = getHeight() - screenH + getTop();
						this.layout(this.getLeft(), screenH - getHeight(),
								this.getRight(), screenH);
					}
				}
				if (getWidth() <= screenW) {
					if (this.getLeft() < 0) {
						disX = getLeft();
						this.layout(0, this.getTop(), 0 + getWidth(),
								this.getBottom());
					} else if (this.getRight() > screenW) {
						disX = getWidth() - screenW + getLeft();
						this.layout(screenW - getWidth(), this.getTop(), screenW,
								this.getBottom());
					}
				}
				if (disX != 0 || disY != 0) {
					trans = new TranslateAnimation(disX, 0, disY, 0);
					trans.setDuration(500);
					this.startAnimation(trans);
				}


				mode = NONE;
				break;
			case MotionEvent.ACTION_POINTER_UP:

				mode = NONE;
				break;
			case MotionEvent.ACTION_MOVE:
			/* 处理拖动 */
				if (mode == DRAG) {
//					float x=event.getX(0);
//					float y=event.getY(0);

//					this.setPosition()
//					if (Math.abs(stop_x - start_x - getLeft()) < 88
//							&& Math.abs(stop_x - start_x - getLeft()) < 88
//							&& Math.abs(stop_y - start_y - getTop()) < 85) {
//
//
//
						if (this.setPosition(stop_x - start_x, stop_y - start_y,
								stop_x + this.getWidth() - start_x, stop_y
										- start_y + this.getHeight())) {
							stop_x = (int) event.getRawX();
							stop_y = (int) event.getRawY();

						}
//
//
//					}

				}
			/* 处理缩放 */
				else if (mode == ZOOM) {
					if (spacing(event) > 10f) {
						afterLenght = spacing(event);
						float gapLenght = afterLenght - beforeLenght;
						if (gapLenght == 0) {

							break;
						} else if (Math.abs(gapLenght) > 5f) {
							if (gapLenght > 0) {
								this.setScale(scale, BIGGER);
							} else {
								this.setScale(scale, SMALLER);
							}

							beforeLenght = afterLenght;

							float rotation = rotation(event);

							ViewHelper.setRotation(this,(float) (ViewHelper.getRotation(this) + (rotation - onDownZoomRotation)));

						}
					}
				}
				break;
		}
		mGestureDetector.onTouchEvent(event);// 执行双击和长按等手势操作
		return true;
	}

	private FrameLayout.LayoutParams viewPream;

	// 取手势中心点
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 取旋转角度
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	/**
	 * 实现处理缩放
	 */
	private void setScale(float temp, int flag) {

		if (flag == BIGGER) {
			this.setFrame(this.getLeft() - (int) (temp * this.getWidth()),
					this.getTop() - (int) (temp * this.getHeight()),
					this.getRight() + (int) (temp * this.getWidth()),
					this.getBottom() + (int) (temp * this.getHeight()));
		} else if (flag == SMALLER) {
			this.setFrame(this.getLeft() + (int) (temp * this.getWidth()),
					this.getTop() + (int) (temp * this.getHeight()),
					this.getRight() - (int) (temp * this.getWidth()),
					this.getBottom() - (int) (temp * this.getHeight()));
		}
	}

	/**
	 * 实现处理拖动
	 *
	 * @return
	 */
	private boolean setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
		return true;
	}



	/**
	 * 手势监听器
	 */
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		/* 双击操作 */
		@Override
		public boolean onDoubleTap(MotionEvent arg0) {
			if (Constant.isDebug)
				Toast.makeText(mContext, "双击操作", Toast.LENGTH_SHORT).show();

			return false;
		}

		/* 长按操作 */
		@Override
		public void onLongPress(MotionEvent ev) {//长按手势并不会触发ACTION_UP事件
			// 从容器中移除当前对象
			parentContainer.removeView(sonContainer);
			sonContainer.removeView(TouchView.this);
			// 把该灯从调出来的灯的集合里移除
			((DiyActivity) mContext).mSelectedLightSA.remove(mLightCount);
		}
	}

}
