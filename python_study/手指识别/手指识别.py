# pip install mediapipe 官网：google.github.io/mediapipe/solutions/hands
# 手掌21个关键点 都是些关节
import cv2
import mediapipe as mp

# 初始化 Mediapipe 手部模块
mp_hands = mp.solutions.hands
hands = mp_hands.Hands()
mp_drawing = mp.solutions.drawing_utils

# 定义方块初始位置
square_x, square_y, square_size = 100, 100, 100

# 打开视频流
cap = cv2.VideoCapture(0)
# 获取画面宽度和高度
width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))


while True:
    ret, frame = cap.read()
    if not ret:
        break

    # 转换为 RGB 格式
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    # 检测手部
    results = hands.process(rgb_frame)

    # 绘制方块
    cv2.rectangle(frame, (square_x, square_y), (square_x + square_size, square_y + square_size), (0, 255, 0), 2)

    if results.multi_hand_landmarks:
        for hand_landmarks in results.multi_hand_landmarks:
            # 绘制手部关键点
            mp_drawing.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)

            # 获取手指坐标（这里使用拇指尖端坐标）
            thumb_tip = hand_landmarks.landmark[mp_hands.HandLandmark.THUMB_TIP]
            finger_x, finger_y = int(thumb_tip.x * frame.shape[1]), int(thumb_tip.y * frame.shape[0])

            # 判断手指是否在方块上
            if square_x < finger_x < square_x + square_size and square_y < finger_y < square_y + square_size:
                # 如果手指在方块上，方块跟随手指移动
                square_x, square_y = finger_x - square_size // 2, finger_y - square_size // 2

    # 显示结果
    cv2.imshow('Video', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 释放资源
cap.release()
cv2.destroyAllWindows()