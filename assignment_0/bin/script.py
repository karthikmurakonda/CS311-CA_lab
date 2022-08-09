import os.path,subprocess
import matplotlib.pyplot as plt

from subprocess import STDOUT,PIPE
from typing import ByteString

def compile_java(java_file):
    subprocess.check_call(['javac', java_file])

def execute_java(java_file, stdin, probability, width):
    java_class,ext = os.path.splitext(java_file)
    cmd = ['java', java_class, str(probability), str(width)]
    proc = subprocess.Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT)
    stdout,stderr = proc.communicate(stdin)
    return  stdout.decode('utf-8')

filename = 'Main.java'
compile_java(filename)

# fixed width
width_list = [x for x in range(100,251,50)]
for w in width_list:
    probabily_list = [x/100 for x in range(0,100,5)]
    time_list = []
    for probability in probabily_list:
        time_list.append(float(execute_java(filename, '', probability, w).strip('\n')))
    print(time_list)
    plt.plot(probabily_list, time_list)
plt.legend(['width = ' + str(w) for w in width_list], loc='upper left')
plt.xlabel('Probability')
plt.ylabel('Time')
plt.title('Fixed Width')
plt.savefig('fixed_width.png')
plt.close()

# variable width
p_list = [0.2,0.5,0.8]
for p in p_list:
    w_list = [x for x in range(2,201)]
    time_list = []
    for width in w_list:
        time_list.append(float(execute_java(filename, '', p, width).strip('\n')))
    print(time_list)
    plt.plot(w_list, time_list)
plt.legend(['probability = ' + str(p) for p in p_list], loc='upper left')
plt.xlabel('Width')
plt.ylabel('Time')
plt.title('Variable width')
plt.savefig('variable_width.png')


