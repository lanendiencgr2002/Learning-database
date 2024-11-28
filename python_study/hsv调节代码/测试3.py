import cv2
import numpy as np

# 创建窗口
cv2.namedWindow('HSV Filter')

# 创建滑动条用于设置 HSV 范围
cv2.createTrackbar('H Min', 'HSV Filter', 0, 180, lambda x: None)
cv2.createTrackbar('H Max', 'HSV Filter', 180, 180, lambda x: None)
cv2.createTrackbar('S Min', 'HSV Filter', 0, 255, lambda x: None)
cv2.createTrackbar('S Max', 'HSV Filter', 255, 255, lambda x: None)
cv2.createTrackbar('V Min', 'HSV Filter', 0, 255, lambda x: None)
cv2.createTrackbar('V Max', 'HSV Filter', 255, 255, lambda x: None)

# 读取图像
img = cv2.imread('img.png')

if img is None:
    print("Error: Image not found.")
    exit()

# 调整图像大小
img = cv2.resize(img, (int(img.shape[1] * 0.5), int(img.shape[0] * 0.5)))

# 创建按钮
button_height = 30
button_width = 50
buttons = [
    {'name': 'H Min-', 'x': 10, 'y': 10},
    {'name': 'H Min+', 'x': 70, 'y': 10},
    {'name': 'H Max-', 'x': 130, 'y': 10},
    {'name': 'H Max+', 'x': 190, 'y': 10},
    {'name': 'S Min-', 'x': 250, 'y': 10},
    {'name': 'S Min+', 'x': 310, 'y': 10},
    {'name': 'S Max-', 'x': 370, 'y': 10},
    {'name': 'S Max+', 'x': 430, 'y': 10},
    {'name': 'V Min-', 'x': 490, 'y': 10},
    {'name': 'V Min+', 'x': 550, 'y': 10},
    {'name': 'V Max-', 'x': 610, 'y': 10},
    {'name': 'V Max+', 'x': 670, 'y': 10}
]


def draw_buttons(frame):
    for button in buttons:
        cv2.rectangle(frame, (button['x'], button['y']),
                      (button['x'] + button_width, button['y'] + button_height),
                      (200, 200, 200), -1)
        cv2.putText(frame, button['name'],
                    (button['x'] + 5, button['y'] + 20),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.4,
                    (0, 0, 0), 1)


def adjust_value(button_name):
    trackbar_name = button_name[:-1]  # Remove the last character (+ or -)
    current_value = cv2.getTrackbarPos(trackbar_name, 'HSV Filter')

    if button_name.endswith('-'):
        new_value = max(0, current_value - 1)
    else:  # button_name.endswith('+')
        if trackbar_name == 'H Max':
            new_value = min(180, current_value + 1)
        else:
            new_value = min(255, current_value + 1)

    cv2.setTrackbarPos(trackbar_name, 'HSV Filter', new_value)


def mouse_callback(event, x, y, flags, param):
    if event == cv2.EVENT_LBUTTONDOWN:
        for button in buttons:
            if button['x'] < x < button['x'] + button_width and button['y'] < y < button['y'] + button_height:
                adjust_value(button['name'])


# 设置鼠标回调
cv2.setMouseCallback('HSV Filter', mouse_callback)

while True:
    # 将 BGR 图像转换为 HSV
    hsv_img = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    # 获取滑动条的值
    h_min = cv2.getTrackbarPos('H Min', 'HSV Filter')
    h_max = cv2.getTrackbarPos('H Max', 'HSV Filter')
    s_min = cv2.getTrackbarPos('S Min', 'HSV Filter')
    s_max = cv2.getTrackbarPos('S Max', 'HSV Filter')
    v_min = cv2.getTrackbarPos('V Min', 'HSV Filter')
    v_max = cv2.getTrackbarPos('V Max', 'HSV Filter')

    # 定义 HSV 范围
    lower_bound = np.array([h_min, s_min, v_min])
    upper_bound = np.array([h_max, s_max, v_max])

    # 创建掩膜
    mask = cv2.inRange(hsv_img, lower_bound, upper_bound)

    # 应用掩膜
    filtered_img = cv2.bitwise_and(img, img, mask=mask)

    # 绘制按钮
    draw_buttons(filtered_img)

    # 显示图像
    cv2.imshow('HSV Filter', filtered_img)

    # 按 'q' 键退出
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 关闭所有窗口
cv2.destroyAllWindows()