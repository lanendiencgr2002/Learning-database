import java.util.Arrays;

public class arrays_classes {
    // 是数组工具类
    public static void main(String[] args) {
        // 数组排序 用的是快速排序
        System.out.println("数组排序：");
        int[] arr = {5,7,1,8,2,7,3,6,9,4};
        Arrays.sort(arr);
        // 转成字符串 底层用的是String Builder
        System.out.println(Arrays.toString(arr));
        
        // 数组二分查找 用的是二分查找
        System.out.println("数组二分查找：");
        int[] arr2 = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        int index = Arrays.binarySearch(arr2, 7);
        System.out.println(index);

    }
}