package microunit;

import java.lang.annotation.Annotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for classes for running unit tests.
 */
public abstract class TestRunner {
    Logger logger = LogManager.getLogger();

    protected Class<?> testClass;

    /**
     * Creates a {@code TestRunner} object for executing the test methods of
     * the class specified.
     *
     * @param testClass the class whose test methods will be executed
     */
    protected TestRunner(Class<?> testClass) {
        this.testClass = testClass;
    }

    /**
     * Returns the list of public methods of the class marked with the
     * annotation specified.
     *
     * @param annotationClass a {@link Class} object representing an annotation
     *                        type
     * @return the list of public methods of the class marked with the
     *         annotation specified
     */
    protected List<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass) {
        return Arrays.stream(testClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .toList();
    }

    /**
     * Runs the test methods of the class.
     */
    public void runTestMethods() {
        try {
            TestResultAccumulator accumulator = new CountingTestResultAccumulator();
            for (Method method : getAnnotatedMethods(Test.class)) {
                //Changed the System.out here and instead of outputting whole method, the method's name is used
                logger.debug("The method {} is being tested",method.getName());
                Object instance = testClass.getConstructor().newInstance();
                invokeTestMethod(method, instance, accumulator);
            }
            //Changed SYstem.out here
            logger.debug(accumulator);
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            //Added logging info about the exception in the invocation of the methods
            logger.warn("Error occured when invoking methods of the object");
            throw new InvalidTestClassException(e);
        }
    }

    /**
     * Invokes a test method on a test class instance accumulating the result of
     * the invocation into a {@link TestResultAccumulator} object.
     *
     * @param testMethod  the test method to be invoked
     * @param instance    the test class instance on which the method is invoked
     * @param accumulator the object to accumulate the result of the invocation
     *                    into
     */
    protected abstract void invokeTestMethod(Method testMethod, Object instance,
            TestResultAccumulator accumulator) throws IllegalAccessException;

}
