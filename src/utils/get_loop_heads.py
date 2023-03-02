import os
import pathlib


def get_loop_heads(jar_file, class_name, method_name):
    """Get loop headers from method

    Args:
        jar_file (str): JAR file
        class_name (str): Class name
        method_name (str): Method name

    Returns:
        loop_heads: List of loop headers
    """
    from jnius import autoclass
    
    URL = autoclass('java.net.URL')
    URLClassLoader = autoclass('java.net.URLClassLoader')
    
    CFGAnalyzer = autoclass('javachecker.CFGAnalyzer')
    
    urls = [URL('jar:file:' + jar_file + '!/')]
    cl = URLClassLoader.newInstance(urls)
    heads = CFGAnalyzer.loopHeaders(cl, class_name, method_name)
    
    return heads.toArray()

if __name__ == '__main__':
    from jnius import autoclass

    os.environ['CLASSPATH'] = str(pathlib.Path(__file__).parent.absolute()) + "../libs/CalculateLoopHeads.jar"

    String = autoclass("java.lang.String")
    calc_lh = autoclass('main.CalculateLoopHeads')

    exit(0)