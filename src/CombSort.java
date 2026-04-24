import java.io.*;
import java.util.*;

public class CombSort {

    // Сам алгоритм с подсчётом итераций и времени
    public static CombSortResult sort(int[] arr) {
        int n = arr.length;
        int gap = n;
        boolean swapped = true;
        long iterations = 0;

        long startTime = System.nanoTime();

        while (gap != 1 || swapped) {
            // Уменьшаем gap
            gap = (int) (gap / 1.3);
            if (gap < 1) gap = 1;

            swapped = false;

            // Сравниваем элементы с текущим gap
            for (int i = 0; i < n - gap; i++) {
                iterations++;
                if (arr[i] > arr[i + gap]) {
                    // swap
                    int temp = arr[i];
                    arr[i] = arr[i + gap];
                    arr[i + gap] = temp;
                    swapped = true;
                }
            }
        }

        long endTime = System.nanoTime();
        long durationMicros = (endTime - startTime) / 1000; // микросекунды

        return new CombSortResult(durationMicros, iterations);
    }

    // Генерация случайных массивов в файлы
    public static void generateTestData(String folderPath, int[] sizes) throws IOException {
        new File(folderPath).mkdirs();
        Random rand = new Random(42); // фиксированный seed для воспроизводимости

        for (int size : sizes) {
            int[] arr = new int[size];
            for (int i = 0; i < size; i++) {
                arr[i] = rand.nextInt(size * 10); // числа до 10*размера
            }
            // Сохраняем в файл
            try (PrintWriter writer = new PrintWriter(new FileWriter(folderPath + "/input_" + size + ".txt"))) {
                for (int val : arr) {
                    writer.println(val);
                }
            }
        }
    }

    // Чтение массива из файла
    public static int[] readArrayFromFile(String filePath) throws IOException {
        List<Integer> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(Integer.parseInt(line.trim()));
            }
        }
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static void runExperiments(String dataFolder, int[] sizes) throws IOException {
        System.out.println("Size\tTime(micros)\tIterations");
        for (int size : sizes) {
            int[] arr = readArrayFromFile(dataFolder + "/input_" + size + ".txt");

            // Усредняем по 10 запускам
            long totalTime = 0;
            long lastIterations = 0;
            for (int run = 0; run < 10; run++) {
                int[] copy = Arrays.copyOf(arr, arr.length);
                CombSortResult res = sort(copy);
                totalTime += res.timeMicros;
                lastIterations = res.iterations; // итерации одинаковы для одинаковых данных
            }
            long avgTime = totalTime / 10;
            System.out.println(size + "\t" + avgTime + "\t" + lastIterations);
        }
    }

    public static void main(String[] args) throws IOException {
        // 1. Генерируем 100 размеров от 100 до 10 000
        int[] sizes = new int[100];
        for (int i = 0; i < 100; i++) {
            sizes[i] = 100 + (i * (9900 / 99));
        }

        // 2. Папка для тестовых данных
        String folder = "comb_sort_data";

        // 3. Создаём тестовые данные (только если папка пуста или её нет)
        File dir = new File(folder);
        if (!dir.exists() || (dir.listFiles() != null && dir.listFiles().length == 0)) {
            System.out.println("Генерация тестовых данных...");
            generateTestData(folder, sizes);
        } else {
            System.out.println("Тестовые данные уже есть, используем их.");
        }

        // 4. Прогрев JVM (чтобы JIT скомпилировал код до основных замеров)
        System.out.println("Прогрев JVM...");
        int[] warmupArr = new int[5000];
        Random rand = new Random();
        for (int i = 0; i < 5000; i++) {
            warmupArr[i] = rand.nextInt(50000);
        }
        for (int i = 0; i < 100; i++) {
            int[] copy = Arrays.copyOf(warmupArr, warmupArr.length);
            sort(copy);
        }
        System.out.println("Прогрев завершён. Начинаем измерения...\n");

        // 5. Основные замеры (усредняем по 10 запускам для каждого размера)
        System.out.println("n\tTime(micros)\tIterations");
        for (int size : sizes) {
            int[] original = readArrayFromFile(folder + "/input_" + size + ".txt");

            long totalTime = 0;
            long lastIterations = 0;

            // Запускаем сортировку 10 раз для усреднения
            for (int run = 0; run < 10; run++) {
                int[] copy = Arrays.copyOf(original, original.length);
                CombSortResult result = sort(copy);
                totalTime += result.timeMicros;
                lastIterations = result.iterations; // итерации для одинаковых данных одинаковы
            }

            long avgTime = totalTime / 10;
            System.out.println(size + "\t" + avgTime + "\t" + lastIterations);
        }
    }
}

class CombSortResult {
    long timeMicros;
    long iterations;

    CombSortResult(long timeMicros, long iterations) {
        this.timeMicros = timeMicros;
        this.iterations = iterations;
    }
}