package runner;

import exceptions.TraceSizeLimitReached;
import gen.*;
import org.apache.commons.cli.*;
import org.json.*;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addRequiredOption("c","class", true, "Class with method.");
        options.addRequiredOption("m", "method", true, "Method name.");
        options.addRequiredOption("j", "jar", true, "Jar File with class and method.");
        options.addOption("s", "samples", true, "Number of samples of a specific function to take");
        options.addOption("seed", "seed", true, "Seed for random objects. If not set, the seed will be randomly generated.");
        options.addOption("strategy", "strategy", true, "Strategy for generating sampling data");
        options.addOption("cegs", "cegs", true, "Strategy for generating sampling data");

        Option customOption = new Option(
            "custom",
            false,
            "Samples to be used as input"
        );
        customOption.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(customOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Trace", options, true);
            System.exit(0);
        }
        File f = new File(cmd.getOptionValue("jar"));
        String className = cmd.getOptionValue("class");
        String methodName = cmd.getOptionValue("method");
        int samples = 1;
        if (cmd.hasOption("s")) {
            samples = Integer.parseInt(cmd.getOptionValue("s"));
        }

        JarContentHandler jar = new JarContentHandler(f);

        List<Class<?>> t = jar
                .getClasses()
                .stream()
                .filter(claz-> claz.getCanonicalName().equals(className)).collect(Collectors.toList());

        if (t.size() != 1) {
            System.out.println("Could not find class or classname was ambiguous.");
            System.exit(0);
        }

        Class<?> clazz = t.get(0);
        List<Method> methods = Arrays.stream(clazz.getMethods())
                .filter(method -> method.getName().equals(methodName))
                .collect(Collectors.toList());

        if (methods.size() != 1) {
            System.out.println("Could not find class or classname was ambiguous.");
            System.exit(0);
        }

        InputGenerator gen =null;
        if (cmd.hasOption("strategy")) {
            if (cmd.getOptionValue("strategy").equals("gaussian")) {
                if (cmd.hasOption("seed")) {
                    gen = new GaussianIntegersER(Long.parseLong(cmd.getOptionValue("seed")));
                } else {
                    gen = new GaussianIntegersER();
                }
            } else if (cmd.getOptionValue("strategy").equals("mvgaussian")) {
                if (cmd.hasOption("seed")) {
                    gen = new MultivariateGaussian(Long.parseLong(cmd.getOptionValue("seed")));
                } else {
                    gen = new MultivariateGaussian();
                }
            } else if (cmd.getOptionValue("strategy").equals("pairanticorr")) {
              if (cmd.hasOption("seed")) {
                gen = new PairwiseAnticorrelated(Long.parseLong(cmd.getOptionValue("seed")));
              } else {
                gen = new PairwiseAnticorrelated();
              }
            } else if (cmd.getOptionValue("strategy").equals("singlevar")) {
                if (cmd.hasOption("seed")) {
                    gen = new SingleHighVariance(Long.parseLong(cmd.getOptionValue("seed")));
                } else {
                    gen = new SingleHighVariance();
                }
            } else { // in case we have a default strategy
                if (cmd.hasOption("seed")) {
                    gen = new EasyRandomGenHandler(Long.parseLong(cmd.getOptionValue("seed")));
                } else {
                    gen = new EasyRandomGenHandler();
                }
            }
        } else {
            if (cmd.hasOption("seed")) {
                gen = new EasyRandomGenHandler(Long.parseLong(cmd.getOptionValue("seed")));
            } else {
                gen = new EasyRandomGenHandler();
            }
        }
        System.out.println(gen.getClass());

        System.out.println("Starting");
        // if(cmd.hasOption("custom")) {
        //     System.out.println("Using custom inputs");

        //     Method method = methods.get(0);
        //     Type[] types = method.getGenericParameterTypes();
        //     Object[] p = new Object[types.length];

        //     for (int i = 0; i < p.length; ++i) {
        //         Type ty = types[i];
        //         String val = cmd.getOptionValues("custom")[i];
        //         if (ty instanceof Class) {
        //             if (ty.equals(Boolean.class) || ty.equals(boolean.class)) {
        //                 p[i] = Boolean.parseBoolean(val);
        //             }  else if (ty.equals(Double.class)|| ty.equals(double.class)) {
        //                 p[i] = Double.parseDouble(val);
        //             } else if (ty.equals(Float.class)|| ty.equals(float.class)) {
        //                 p[i] = Float.parseFloat(val);
        //             } else if (ty.equals(Long.class)|| ty.equals(long.class)) {
        //                 p[i] = Long.parseLong(val);
        //             } else if (ty.equals(Integer.class) || ty.equals(int.class)) {
        //                 p[i] = Integer.parseInt(val);
        //             } else {
        //                 // Currently does not support this
        //                 p[i] = null;
        //             }
        //         }
        //     }

        //     try {
        //         Main.invoke(method, p);
        //     } catch (InvocationTargetException ex) {
        //         Throwable e = ex.getCause();
        //         if (e instanceof TraceSizeLimitReached) {
        //             //System.err.println("Caught ThreadDeath exception. Tracing of sample " + i + " was interrupted.");
        //         } else {
        //             throw ex;
        //         }
        //     }

        // } else 
        if(cmd.hasOption("cegs")) { // If we have a CEGIS loop
            System.out.println("CEGS strategy");
            String argumentsFile = cmd.getOptionValue("cegs");

            Method method = methods.get(0); //
            Type[] types = method.getGenericParameterTypes();

            gen = new CEGSGenerator(argumentsFile);

            Object[] p = gen.nextSampleArguments(types);

            try {
                Main.invoke(method, p);
            } catch (InvocationTargetException ex) {
                Throwable e = ex.getCause();
                if (e instanceof TraceSizeLimitReached) {
                    //System.err.println("Caught ThreadDeath exception. Tracing of sample " + i + " was interrupted.");
                } else {
                    throw ex;
                }
            }

            if (cmd.hasOption("seed")) {
                gen = new CEGSMVGaussianGen(argumentsFile,Long.parseLong(cmd.getOptionValue("seed")));
            } else {
                gen = new CEGSMVGaussianGen(argumentsFile);
            }

            for (int i = 0; i < samples; ++i) {

                p = gen.nextSampleArguments(types);
                
                try {
                    Main.invoke(method, p);
                } catch (InvocationTargetException ex) {
                    Throwable e = ex.getCause();
                    if (e instanceof TraceSizeLimitReached) {
                        //System.err.println("Caught ThreadDeath exception. Tracing of sample " + i + " was interrupted.");
                    } else {
                        throw ex;
                    }
                }
            }

        } else {
            System.out.println("Other strategies");
            Method method = methods.get(0); //
            System.out.print("Method ");
            System.out.println(method);
            Type[] types = method.getGenericParameterTypes();
            System.out.print("Type[0] ");
            System.out.println(types[0]);

            for (int i = 0; i < samples; ++i) {

                Object[] p = gen.nextSampleArguments(types);

                for (int j = 0; i < p.length; ++i) {
                    int k = (Integer) p[j];
                    System.out.println(k);
                }
                
                try {
                    Main.invoke(method, p);
                } catch (InvocationTargetException ex) {
                    Throwable e = ex.getCause();
                    if (e instanceof TraceSizeLimitReached) {
                        //System.err.println("Caught ThreadDeath exception. Tracing of sample " + i + " was interrupted.");
                    } else {
                        throw ex;
                    }
                }
            }
        }

    }
    

    private static void defaultSamplingStrategy(Long seed){
        if (seed == null) {

        }

    }

    public static void invoke(Method m, Object[] p) throws InvocationTargetException, IllegalAccessException {
        m.invoke(null,p);
    }
}