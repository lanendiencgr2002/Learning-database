import cv2, numpy as np
cv2.namedWindow('HSV')
for c in 'HSV': cv2.createTrackbar(c, 'HSV', 0, 255 if c!='H' else 180, lambda x:None)
img = np.zeros((300, 300, 3), np.uint8)
while cv2.waitKey(1) != ord('q'):
    hsv = [cv2.getTrackbarPos(c, 'HSV') for c in 'HSV']
    img[:] = cv2.cvtColor(np.uint8([[hsv]]), cv2.COLOR_HSV2BGR)
    cv2.imshow('HSV', img)
cv2.destroyAllWindows()
