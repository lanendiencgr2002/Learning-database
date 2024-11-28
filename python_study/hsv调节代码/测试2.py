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

    # 显示原图和筛选后的图像
    # cv2.imshow('Original Image', img)
    cv2.imshow('Filtered Image', filtered_img)

    # 按 'q' 键退出
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 关闭所有窗口
cv2.destroyAllWindows()