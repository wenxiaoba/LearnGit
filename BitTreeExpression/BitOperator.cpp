#include"BitOperator.h"
#include<ctype.h>
#include<cstdlib>

bool checkBrackets(char *input, int l, int r)
{
	int flag = 0;
	char c;
	int i;
	for (i = l; i <= r; i++)
	{
		c = input[i];
		if (c == '(')
			flag++;
		else if (c == ')')
			flag--;
		if (flag == 0)
			break;
	}
	if ((flag == 0) && (i >= r))
		return true;
	else return false;
}

//----------------------------------------------------
int findNode(char *input, int l, int r)
{
	int index = 0;
	char data;
	int flag = 0;
	bool finded = false;
	for (int i = l; i <= r; i++)
	{
		data = input[i];
		switch (data)
		{
		case '(': flag++; break;
		case ')': flag--; break;
		case '+':
		case '-': if (!flag && i != l){ finded = true; index = i; break; }
				  //i!=l : eleminite the negative number;like (- 4 / 5) ,the '/''s priority is lower than '-''s;
		case '*':
		case '/': if (!finded && !flag) { index = i; break; }
				  //There the finded can not be set to true ,when it is true,like 2*3*4 will be wrong.
		}
	}
	return index;
}

//----------------------------------------------------
Tree *buildTrees(char *input, int l, int r)
{
	if ((input[l] == '(') && (input[r] == ')') && (checkBrackets(input, l, r)))
	{
		l++;
		r--;
	}
	int i;
	int  index;
	for (i = l; i <= r; i++)
	{
		if ((i == l) && ((input[i] == '-') || (input[i] == '+')))
			continue;
		if (!isdigit(input[i]) && (input[i] != '.'))
			break;
	}
	if (i >= r)
	{
		Tree *T = new Tree;
		T->data.number = atol(input + l);
		T->lchild = T->rchild = NULL;
		return T;
	}
	else
	{
		index = findNode(input, l, r);
		Tree *t = new Tree;
		t->data.symbol = input[index];
		t->lchild = buildTrees(input, l, index - 1);
		t->rchild = buildTrees(input, index + 1, r);
		return t;
	}

}
//----------------------------------------------------
double preOrder(Tree *tree)
{
	if (tree == NULL)
		return 0;
	if ((tree->lchild == NULL) && (tree->rchild == NULL))
		return tree->data.number;
	double lresult = preOrder(tree->lchild);
	double rresult = preOrder(tree->rchild);
	double result = 0.0;
	switch (tree->data.symbol)
	{
	case '-': result = lresult - rresult; break;
	case '+': result = lresult + rresult; break;
	case '*': result = lresult * rresult; break;
	case '/': if(rresult != 0) result = lresult / rresult; break;
	}
	return result;
}
//----------------------------------------------------
void FreeTree(Tree *T)
{
	if (T != NULL)
	{
		FreeTree(T->lchild);
		FreeTree(T->rchild);
		delete(T);
	}
}