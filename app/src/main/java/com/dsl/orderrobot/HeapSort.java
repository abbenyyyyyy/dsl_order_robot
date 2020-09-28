package com.dsl.orderrobot;

class HeapSort {
    public static int[] sort(int[] arr) {
        int n = arr.length;
        // 初始化堆
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);
        //排序
        for (int i = n - 1; i >= 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            heapify(arr, i, 0);
        }
        return arr;
    }

    static void heapify(int arr[], int n, int i) {
        int largest = i; // 初始化根
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        // left > root
        if (l < n && arr[l] > arr[largest])
            largest = l;
        // right > root
        if (r < n && arr[r] > arr[largest])
            largest = r;
        // 如果最大值不是根节点，调整
        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            // heapify
            heapify(arr, n, largest);
        }
    }
}
