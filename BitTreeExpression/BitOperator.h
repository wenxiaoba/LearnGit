#ifndef BITOPERATOR_H
#define BITOPERATOR_H

typedef union
{
	char symbol;
	double number;
}UN;

typedef struct TREE
{
	UN data;
	TREE *lchild;
	TREE *rchild;
}Tree;

bool checkBrackets(char *input, int l, int r);
int findNode(char *input, int l, int r);
Tree *buildTrees(char *input, int l, int r);
void getInput(char *input);
double preOrder(Tree *tree);
void FreeTree(Tree *T);




#endif