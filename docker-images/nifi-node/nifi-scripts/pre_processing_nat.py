#import java.io
#from org.apache.commons.io import IOUtils
#from java.nio.charset import StandardCharsets
from org.apache.nifi.processor.io import StreamCallback
import pandas as pd


# Define a subclass of StreamCallback for use in session.write()
class PyStreamCallback(StreamCallback):
    def __init__(self):
        pass

    def process(self, inputStream, outputStream):

        #input_data = IOUtils.toString(inputStream, StandardCharsets.UTF_8)  # .split('\n')
        # df1 = csv.reader(input_data, delimiter=',')
        # dataset_nation = pd.read_csv(inputStream, StandardCharsets.UTF_8)

        df1 = pd.read_csv(inputStream)
        df2 = df1[['dimessi_guariti', 'tamponi']].shift(periods=1, fill_value=0)
        df1[['dimessi_guariti', 'tamponi']] = df1[['dimessi_guariti', 'tamponi']].subtract(
            df2[['dimessi_guariti', 'tamponi']])

        result = df1.to_csv(flowFile.getAttribute('filename'), sep=',', index=False)
        if result is None:
            print('No matching found.')
        else:
            outputStream.write(bytearray(result.encode('utf-8')))
# end class

flowFile = session.get()
if flowFile != None:
    flowFile = session.putAttribute(flowFile, flowFile.getAttribute('filename'))
    flowFile = session.write(flowFile, PyStreamCallback())
    session.transfer(flowFile, REL_SUCCESS)
else:
    session.transfer(flowFile, REL_FAILURE)
session.commit()

#./data/nifi-scripts/pre_proccessing_nat.py