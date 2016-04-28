from baselib import BaseLib

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

        self.set_result(words)