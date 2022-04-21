package microunit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class for running unit tests without support for expected exceptions.
 */
public class BasicTestRunner extends TestRunner {
    Logger logger = LogManager.getLogger();

    /**
     * Creates a {@code BasicTestRunner} object for executing the test methods
     * of the class specified.
     *
     * @param testClass the class whose test methods will be executed
     */
    public BasicTestRunner(Class<?> testClass) {
        super(testClass);
    }

    @Override
    public void invokeTestMethod(Method testMethod, Object instance, TestResultAccumulator results)
            throws IllegalAccessException {
        try {
            //Added logigng here to inform that testing is starting
            logger.info("Starting testing instance of the class {}",instance.getClass().getName());
            testMethod.invoke(instance);
            results.onSuccess(testMethod);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            //Added here logging that informs that exception occured
            logger.error("Exception was thrown when trying to invoke methods of the test instance. Error is:");
            for(int i = 0 ; i< cause.getStackTrace().length;i++){

                //Substituted from cause.printStackTrace(SYstem.out) to log4j logging with trace

                logger.trace(cause.getStackTrace()[i]);
            }
            if (cause instanceof AssertionError) {
                logger.warn("The cause of exception is of AssertionError class");
                results.onFailure(testMethod);
            } else {

                results.onError(testMethod);
            }
        }
    }

    // CHECKSTYLE:OFF
    public static void main(String[] args) throws Exception {
        Class<?> testClass = Class.forName(args[0]);
        new BasicTestRunner(testClass).runTestMethods();
    }

}
