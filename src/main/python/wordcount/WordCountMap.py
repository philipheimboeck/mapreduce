from .baselib import BaseLib
from .TupleList import TupleList

class WordCountMap(BaseLib):
    """
    WordCount Map Task demonstration
    """
    def run(self):

        text = self.in_parameters[0]

        # Add words to list
        words = []
        for word in text.split(' '):
            words.append([word, 1])

        list = TupleList(words)

        self.set_result(list)
        self.exec_lib("")