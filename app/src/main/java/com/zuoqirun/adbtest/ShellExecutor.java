package com.zuoqirun.adbtest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShellExecutor {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * 执行 Shell 命令，异步返回标准输出和标准错误输出
     *
     * @param command Shell 命令字符串
     * @param listener 命令执行的回调监听器
     */
    public static void executeCommandAsync(String command, CommandListener listener) {
        EXECUTOR.submit(() -> {
            try {
                // 创建一个 Process 建立一个 Shell 进程
                Process process = Runtime.getRuntime().exec(command);

                // 执行一个后台线程来处理输出和错误
                handleProcessOutput(process, listener);

                // 等待进程结束
                int exitCode = process.waitFor();
                if (listener != null) {
                    listener.onProcessFinished(exitCode);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * 同步执行 Shell 命令
     *
     * @param command Shell 命令字符串
     * @return 命令执行的结果（标准输出和标准错误输出）
     */
    public static CommandResult executeCommandSync(String command) {
        CommandResult result = new CommandResult();
        try {
            // 创建一个 Process 建立一个 Shell 进程
            Process process = Runtime.getRuntime().exec(command);

            // 得到标准输出流和错误输出流，并将其转化为字符串
            result.stdout = getOutput(process.getInputStream());
            result.stderr = getOutput(process.getErrorStream());

            // 等待进程结束并获取退出码
            int exitCode = process.waitFor();
            result.exitCode = exitCode;

        } catch (Exception e) {
            result.stderr = e.getMessage();
            result.exitCode = -1; // 表示执行失败
        }

        return result;
    }

    /**
     * 处理异步进程的标准输出和错误输出
     */
    private static void handleProcessOutput(Process process, CommandListener listener) {
        // 启动线程处理标准输出
        new Thread(() -> {
            try {
                InputStream inputStream = process.getInputStream();
                listener.onStdout(getOutput(inputStream));
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        }).start();

        // 启动线程处理标准错误输出
        new Thread(() -> {
            try {
                InputStream errorStream = process.getErrorStream();
                listener.onStderr(getOutput(errorStream));
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 从输入流中读取并返回字符串
     */
    private static String getOutput(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * 命令执行结果类，包含标准输出、错误输出和退出码
     */
    public static class CommandResult {
        public String stdout;   // 标准输出
        public String stderr;   // 标准错误输出
        public int exitCode;    // 进程退出码
    }

    /**
     * 命令执行回调监听器
     */
    public interface CommandListener {
        void onStdout(String output);   // 标准输出回调
        void onStderr(String error);    // 标准错误输出回调
        void onProcessFinished(int exitCode); // 进程结束回调
        void onError(String errorMessage);   // 出错回调
    }
}
