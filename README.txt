Ryan Oman
CSE 473
Assignment 4
12/09/2010

The plots and their data are stored in the pdf files along with this one. They are also
available in the excel file included in with this file.

************************************************************************************************
Implementation Notes
************************************************************************************************
In order to implement Decision Tree Learning, I modified/added the following classes:

------------------------
interface Node
------------------------
This interface is implemented by the LeafNode and DecisionNode classes. It is used 
so that both DecisionNodes and LeafNodes can be stored in the same way, as a Node.

------------------------
class DecisionNode
------------------------
This class stores the integer attribute that the example set was split on for the 
node. All Examples whose attribute was false are in the left subtree, all Examples
whose attribute was true are in the right subtree.

------------------------
class LeafNode
------------------------
This class stores a boolean value which is the is the label/classification to be 
guessed if the predict function reaches such a given node. It has no children 
pointers because it is the end of the prediction process.

------------------------
class DecisionTree
------------------------
This class is used to perform the training and prediction for the sets of Examples.
The train used the recursive helper buildTree. The examples are passed to this 
helper as a HashMap of ArrayLists. The hash only contains two keys, true and false.
The ArrayList that is returned by 'examples.get(true)' only contains Examples that 
have a label of 'true'. The same is true for the ArrayList with the key false, they
all have a label of 'false'.

------------------------
class Utils
------------------------
This class store all of the static methods for the DecisionTree class. It does all of
the heavy lifting for the DecisionTree class. It contains the method to get the most
important attribute to split on, and calculate the gain/entropy for splitting on each
attribute. It also contains the method for dividing examples based on an attribute, and
doing the final plurality-value for determining the value a LeafNode uses.

************************************************************************************************
Bugs and Problems I Encountered
************************************************************************************************
	I had some problems correctly implementing the info gain calculation. I also had a truncation
bug that caused my gain values to always be 0.0. The most elusive bug in this project for me was
the one that caused my tree to perform slightly worse than it should have because when I removed
the attribute I split on in one branch, it caused it to be removed in the ArrayList when building
the other branches because ArrayLists are stored on the Heap.

************************************************************************************************
Graph 1 - Plot of accuracies for different methods/datasets/maxdepths - graph1.pdf, data1.pdf
************************************************************************************************
	I got the best test and training set accuracy when using Info Gain as my splitting 
rule. Info Gain provided the best accuracy, because it splits more intelligently. More 
specifically, it always splits on the attribute that would provide the most new 
information about the data set. 

	The accuracy converges to it's maximum, starting around a max depth of 9 or 10 for the 
test data set. It reaches it's maximum at 16, and then decreases slightly. Prior to a max 
depth of 9 or 10 and the decision tree is too short, and has too many nodes that randomly 
chose what classification should be put at the LeafNode(ie: there were a mix of positively 
and negatively labeled examples left).
	The accuracy decreases slightly after it's maximum of 16 mainly because of 
overfitting to the training data. The tree performs better on the training data when
making predictions because it is based off of the training data. When running on the test
data, there may be some solutions that the training data provides that are specific for
that set, which is why it is 'overfit' to that set.

	The accuracy reaches its maximum for the training set at a maxDepth of 15. It then
stays pretty much constant for any depth greater than 15. This is because it is not affected
overfitting, since the training set is what the tree was built off of. When shorter than
15, the tree makes too many guesses when it could be performing more splits on the data,
meaning it has a mix of positively and negatively labeled examples when the tree has to
determine what the classification should be at that level.

************************************************************************************************
Graph 2 - Difference in Training and Test Set Accuracy - graph2.pdf, data2.pdf
************************************************************************************************
	I got the largest variation between the running the same classifier on the training and test
data sets when using the Random splitting rule at a depth of 30. This makes sense, because 
the random splitting rule tends to generate a tree that is more overfit to the training set, 
since a with the random splitting method you end up with more LeafNodes that have to make a 
classification based off of incomplete data (they have a mix of positively and negatively 
labeled training data). And when you have to use majority rules to determine the classification,
it is more likely to fit to the training data set than to another data set.
	It also makes sense that the curve for the Random difference trends upwards, since when 
the tree is shorter, it is more just a random guess, and as the height increases, it becomes more
of overfit to guess based off of the training data.


************************************************************************************************
Extra Credit - Random Forests - graph3.pdf, data3.pdf
************************************************************************************************
The Random Forests performed progressively better as the depth increased. The prediction for the
training set reached 100% accuracy at height 27 and stayed constant at that after height 29. The
test set accuracy pretty much stayed between 97% and 98% accuracy from max depth 29 and greater. 
From this data, it seems that The Info Gain method is still a better option that Random Forests 
for several reasons:
	- It has much higher accuracies at smaller maxDepths (Random Forests reaches its max with 
	  depth 29, Info Gain reaches it around height 15.
	- Info Gain still has a better accuracy for all maxDepths less than 100 for the test data.
	- Info Gain is much faster, since you only have to run the predict and train algorithms
	  on one tree instead of 100.
The one prediction that the Random Forest did better than Info Gain was predicting on the 
training set. The Random Forest reaches 100% accuracy, whereas the Info Gain trees are 
just short of 100%. This is most likely because when an wrong answer is provided in a 
decision tree, it is most likely to be specific to that tree structure; therefore, since the
Random Forest has the majority rules method, it ignores these independent errors that a 
single tree might return.