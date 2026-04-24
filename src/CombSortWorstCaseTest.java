import java.util.*;

public class CombSortWorstCaseTest {

    public static void main(String[] args) {
        System.out.println("=== ТЕСТ ХУДШЕГО СЛУЧАЯ ===\n");

        int n = 100;

        int[] worstArr = new int[n];
        for (int i = 0; i < n; i++) {
            worstArr[i] = n - i;
        }

        int[] randomArr = new int[n];
        Random rand = new Random(42);
        for (int i = 0; i < n; i++) {
            randomArr[i] = rand.nextInt(1000);
        }


        int[] nearlySortedArr = new int[n];
        for (int i = 0; i < n; i++) {
            nearlySortedArr[i] = i;
        }

        for (int i = 0; i < 5; i++) {
            int pos = rand.nextInt(n - 1);
            int temp = nearlySortedArr[pos];
            nearlySortedArr[pos] = nearlySortedArr[pos + 1];
            nearlySortedArr[pos + 1] = temp;
        }


        System.out.println("Результаты Comb Sort на n = " + n + ":\n");

        testOnArray("ХУДШИЙ СЛУЧАЙ (обратный порядок)", worstArr);
        testOnArray("СЛУЧАЙНЫЙ МАССИВ", randomArr);
        testOnArray("ПОЧТИ ОТСОРТИРОВАННЫЙ", nearlySortedArr);


        System.out.println("\n=== ДЛЯ СРАВНЕНИЯ: Пузырьковая сортировка ===");
        int[] bubbleArr = new int[n];
        for (int i = 0; i < n; i++) {
            bubbleArr[i] = n - i;
        }
        testBubbleSort(bubbleArr);
    }

    public static void testOnArray(String name, int[] arr) {
        int[] copy = Arrays.copyOf(arr, arr.length);
        CombSortResult result = combSortWithCount(copy);

        System.out.println(name + ":");
        System.out.println("  Итераций: " + result.iterations);
        System.out.println("  n²/2 = " + (arr.length * arr.length / 2));
        System.out.println("  n²/4 = " + (arr.length * arr.length / 4));
        System.out.println("  Отношение к n²/2: " + (result.iterations * 100.0 / (arr.length * arr.length / 2)) + "%");
        System.out.println();
    }

    public static CombSortResult combSortWithCount(int[] arr) {
        int n = arr.length;
        int gap = n;
        boolean swapped = true;
        long iterations = 0;

        long startTime = System.nanoTime();

        while (gap != 1 || swapped) {
            gap = (int) (gap / 1.3);
            if (gap < 1) gap = 1;

            swapped = false;

            for (int i = 0; i < n - gap; i++) {
                iterations++;
                if (arr[i] > arr[i + gap]) {
                    int temp = arr[i];
                    arr[i] = arr[i + gap];
                    arr[i + gap] = temp;
                    swapped = true;
                }
            }
        }

        long endTime = System.nanoTime();
        long timeMicros = (endTime - startTime) / 1000;

        return new CombSortResult(iterations, timeMicros);
    }

    public static void testBubbleSort(int[] arr) {
        int n = arr.length;
        long iterations = 0;
        boolean swapped;

        long startTime = System.nanoTime();

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                iterations++;
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }

        long endTime = System.nanoTime();
        long timeMicros = (endTime - startTime) / 1000;

        System.out.println("Пузырьковая сортировка на обратном массиве:");
        System.out.println("  Итераций: " + iterations);
        System.out.println("  n²/2 = " + (n * n / 2));
        System.out.println("  Время: " + timeMicros + " мкс");
    }

    static class CombSortResult {
        long iterations;
        long timeMicros;

        CombSortResult(long iterations, long timeMicros) {
            this.iterations = iterations;
            this.timeMicros = timeMicros;
        }
    }
}