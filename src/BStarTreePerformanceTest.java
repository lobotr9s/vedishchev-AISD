import java.io.*;
import java.util.*;

public class BStarTreePerformanceTest {

    static class Measurement {
        long time;
        int operations;

        Measurement(long time, int operations) {
            this.time = time;
            this.operations = operations;
        }
    }

    public static void main(String[] args) {
        // Создание B*-дерева с t=3 (максимум 5 ключей в узле)
        BStarTree tree = new BStarTree(3);

        // Генерация массива из 10000 случайных чисел
        int[] array = generateRandomArray(10000);

        // Списки для хранения результатов измерений
        List<Measurement> insertMeasurements = new ArrayList<>();
        List<Measurement> searchMeasurements = new ArrayList<>();
        List<Measurement> deleteMeasurements = new ArrayList<>();

        // 1. Измерение времени вставки
        System.out.println("Измерение времени вставки 10000 элементов...");
        for (int i = 0; i < array.length; i++) {
            long startTime = System.nanoTime();
            tree.insert(array[i]);
            long endTime = System.nanoTime();

            insertMeasurements.add(new Measurement(
                    endTime - startTime,
                    tree.getOperationCount()
            ));
        }

        // 2. Измерение времени поиска (100 случайных элементов)
        System.out.println("Измерение времени поиска 100 элементов...");
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int index = random.nextInt(array.length);
            long startTime = System.nanoTime();
            tree.search(array[index]);
            long endTime = System.nanoTime();

            searchMeasurements.add(new Measurement(
                    endTime - startTime,
                    tree.getOperationCount()
            ));
        }

        // 3. Измерение времени удаления (1000 случайных элементов)
        System.out.println("Измерение времени удаления 1000 элементов...");
        for (int i = 0; i < 1000; i++) {
            int index = random.nextInt(array.length);
            long startTime = System.nanoTime();
            tree.delete(array[index]);
            long endTime = System.nanoTime();

            deleteMeasurements.add(new Measurement(
                    endTime - startTime,
                    tree.getOperationCount()
            ));
        }

        // Вычисление средних значений
        double avgInsertTime = calculateAverageTime(insertMeasurements);
        double avgInsertOps = calculateAverageOperations(insertMeasurements);

        double avgSearchTime = calculateAverageTime(searchMeasurements);
        double avgSearchOps = calculateAverageOperations(searchMeasurements);

        double avgDeleteTime = calculateAverageTime(deleteMeasurements);
        double avgDeleteOps = calculateAverageOperations(deleteMeasurements);

        // Сохранение результатов в файл
        saveResultsToFile(insertMeasurements, searchMeasurements, deleteMeasurements);

        // Вывод результатов
        System.out.println("\n=== РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ B*-ДЕРЕВА ===");
        System.out.println("Добавление элементов:");
        System.out.printf("  Среднее время: %.3f мкс\n", avgInsertTime / 1000.0);
        System.out.printf("  Среднее количество операций: %.2f\n", avgInsertOps);

        System.out.println("\nПоиск элементов:");
        System.out.printf("  Среднее время: %.3f мкс\n", avgSearchTime / 1000.0);
        System.out.printf("  Среднее количество операций: %.2f\n", avgSearchOps);

        System.out.println("\nУдаление элементов:");
        System.out.printf("  Среднее время: %.3f мкс\n", avgDeleteTime / 1000.0);
        System.out.printf("  Среднее количество операций: %.2f\n", avgDeleteOps);

        System.out.println("\nТеоретическая сложность операций: O(log n)");
        System.out.println("B*-дерево заполнено минимум на 2/3, что делает его более эффективным");
        System.out.println("по использованию памяти по сравнению с обычным B-деревом.");
    }

    private static int[] generateRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(100000);
        }
        return array;
    }

    private static double calculateAverageTime(List<Measurement> measurements) {
        long totalTime = 0;
        for (Measurement m : measurements) {
            totalTime += m.time;
        }
        return (double) totalTime / measurements.size();
    }

    private static double calculateAverageOperations(List<Measurement> measurements) {
        int totalOps = 0;
        for (Measurement m : measurements) {
            totalOps += m.operations;
        }
        return (double) totalOps / measurements.size();
    }

    private static void saveResultsToFile(
            List<Measurement> insertMeasurements,
            List<Measurement> searchMeasurements,
            List<Measurement> deleteMeasurements) {

        try (PrintWriter writer = new PrintWriter("bstree_results.csv")) {
            writer.println("Operation,Time(ns),Operations");

            for (Measurement m : insertMeasurements) {
                writer.printf("INSERT,%d,%d\n", m.time, m.operations);
            }

            for (Measurement m : searchMeasurements) {
                writer.printf("SEARCH,%d,%d\n", m.time, m.operations);
            }

            for (Measurement m : deleteMeasurements) {
                writer.printf("DELETE,%d,%d\n", m.time, m.operations);
            }

            System.out.println("\nРезультаты сохранены в файл 'bstree_results.csv'");
        } catch (FileNotFoundException e) {
            System.err.println("Ошибка сохранения файла: " + e.getMessage());
        }
    }
}