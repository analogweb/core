package org.analogweb.util.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * java.util.loggingを、コンフィギュレーションファイル(logging.properties)
 * を使用しないでコンフィギュレーション可能なクラスです。<br/>
 * コンフィギュレーションファイルから{@link LogManager}の設定が読み込まれる場合
 * システムクラスローダがロードを行うため、コンフィギュレーション内で使用するクラスはシステム
 * クラスローダが参照できるパスにが存在していなければならず、また、コンフィギュレーション
 * ファイルは各VMで共有されます。これは、Webアプリケーションなどでロギングを行う場合
 * 大変不便です。
 * このクラスのサブクラスは、下記の名前でクラスパスに配置する必要があります。
 * <li>jp.analog.util.JulLogConfigImpl</li>
 * @author snowgoose
 */
public abstract class JulLogConfig {

    /**
     * 現在のVMにおける設定に依存した{@link Logger}を生成する
     * {@link JulLogConfig}です。
     */
    public static final JulLogConfig SIMPLE = new JulLogConfig() {
        @Override
        public void configure(ClassLoader classLoader) {
            // nop.
        }

        @Override
        public Logger createLogger(String name) {
            return Logger.getLogger(name);
        }
    };

    public JulLogConfig() {
        // nop.
    }

    public void configure(ClassLoader classLoader) {
        LogManager manager = LogManager.getLogManager();
        resetLogManager(manager);
        configureInternal(manager);
    }

    protected void configureInternal(LogManager manager) {
        // only reset LogManager.
    }

    protected void resetLogManager(LogManager manager) {
        manager.reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        for (Handler handler : globalLogger.getHandlers()) {
            globalLogger.removeHandler(handler);
        }
    }

    public Logger createLogger(String name) {
        LogManager manager = LogManager.getLogManager();
        return createLoggerInternal(name, manager);
    }

    protected Logger createLoggerInternal(String name, LogManager manager) {
        Logger logger = Logger.getLogger(name);
        logger.setLevel(Level.INFO);
        logger.addHandler(createConsoleHandler());
        manager.addLogger(logger);
        return logger;
    }

    private ConsoleHandler createConsoleHandler() {
        ConsoleHandler console = new ConsoleHandler();
        console.setFormatter(new JulLogFormatter());
        console.setLevel(Level.INFO);
        return console;
    }

}
