package fsbench;

import org.apache.commons.cli.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MigrationBenchmark {

    public static int THREADS;

    public static String FS;
    public static String CASE;
    public static String BASE_PATH;

    public static void main(String[] args) throws Exception {
        parseOptions(args);

        BASE_PATH = "/" + FS + "/" + CASE + "-" + THREADS;
        Files.createDirectory(Paths.get(BASE_PATH));

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        ExecutorCompletionService<TaskResult> completionService = new ExecutorCompletionService(executorService);
        for(int i = 0; i < THREADS; ++i) {
            completionService.submit(new MigrationTask(10 * 1024 * 1024 / THREADS)); // 10M
        }

        List<TaskResult> resultList = new ArrayList();

        for (int i = 0; i < THREADS; ++i) {
            TaskResult result = completionService.take().get();
            if (result != null)
                resultList.add(result);
        }

        stat(resultList);

        executorService.shutdown();
    }

    private static void stat(List<TaskResult> resultList) {
        TaskResult totalResult = new TaskResult();
        for(TaskResult result : resultList) {
            totalResult.totalFiles += result.totalFiles;
            totalResult.totalSize += result.totalSize;
            totalResult.seconds += result.seconds;
            totalResult.milliseconds += result.milliseconds;
            totalResult.microseconds += result.microseconds;
            totalResult.nanoseconds += result.nanoseconds;
        }

        System.out.println(resultList);
        System.out.println(totalResult);
    }

    private static void parseOptions(String[] args) {
        Options options = new Options();

        Option fs = new Option("fs", "fs", true, "target file system");
        fs.setRequired(true);
        options.addOption(fs);

        Option cs = new Option("case", "cs", true, "case");
        cs.setRequired(true);
        options.addOption(cs);

        Option threads = new Option("thread", "t", true, "thread number");
        threads.setRequired(true);
        options.addOption(threads);

        CommandLineParser parser = new BasicParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("fsbench", options);

            System.exit(1);
            return;
        }

        FS = cmd.getOptionValue("fs");
        CASE = cmd.getOptionValue("case");
        THREADS = Integer.valueOf(cmd.getOptionValue("thread"));

        System.out.println(FS);
        System.out.println(CASE);
        System.out.println(THREADS);
    }
}
