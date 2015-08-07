package com.sr178.game.server.constant;

public class SystemConstantErrorCode {

    //房间被锁定，不允许进入
   public static final int ROOM_IS_LOCK = 2;
   public static final String ROOM_IS_LOCK_DESC = "房间被锁定，不允许进入";
   
   //用户已经进入过房间 不能重复进入
   public static final int USER_HAVE_ENTER = 3;
   public static final String USER_HAVE_ENTER_DESC = "用户已经进入过房间 不能重复进入";
   
   //坐位已经被占领
   public static final int SEAT_HAVE_OCCPY = 4;
   public static final String SEAT_HAVE_OCCPY_DESC = "坐位已经被占领";
   
   //原来的坐位没有你
   public static final int SEAT_IS_NOT_YOU = 5;
   public static final String SEAT_IS_NOT_YOU_DESC = "原来的坐位没有你";
   
	//父房间不存在
   public static final int PARENT_ROOM_IS_NOT_EXIST = 6;
   public static final String PARENT_ROOM_IS_NOT_EXIST_DESC = "父房间不存在";
   
   //房间满人了
   public static final int ROOM_FULL = 7;
   public static final String ROOM_FULL_DESC = "房间已经没有空位了";
   
   //座位上还有人不能关闭卡片
   public static final int SEAT_HAS_USER = 8;
   public static final String SEAT_HAS_USER_DESC = "座位上还有人不能关闭卡片";
   
   //座位状态异常
   public static final int SEAT_STATE_ERROR = 9;
   public static final String SEAT_STATE_ERROR_DESC = "座位状态异常";
   
   //座位关闭了
   public static final int SEAT_CLOSED = 10;
   public static final String SEAT_CLOSED_DESC = "座位关闭了";
   
	//房间满员不能进入
   public static final int ROOM_IS_FULL = 11;
   public static final String ROOM_IS_FULL_DESC = "房间满员不能进入";
   
 //座位关闭了
   public static final int SEAT_LOCKED = 12;
   public static final String SEAT_LOCKED_DESC = "座位锁定了";
   
 //座位上还有人不能锁定座位
   public static final int SEAT_IS_NOT_EMPTY = 13;
   public static final String SEAT_IS_NOT_EMPTY_DESC = "座位被占,不能锁定";
}
