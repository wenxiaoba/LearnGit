#include<iostream>
#include"BitOperator.h"
//#include<string>
using namespace std;
#define MAXSIZE 10000
char input[MAXSIZE];
bool issymbol(char symbol);
int main()
{
	Tree *t;
	cout << "please enter your expression : " << endl;
	while (true)
	{
		cout << " > ";
		getInput(input);
		
		if (!strcmp(input, "88"))
		{
			cout << input << endl;
			break;
		}
		t = buildTrees(input,0,strlen(input)-1);
		double result = preOrder(t);
		cout << "-->" << result << endl;
		FreeTree(t);
	}
	return 0;
}

void getInput(char *input)
{
	char ch;
	char *p = input;
	while (ch = getchar())
	{
		if (ch == '#')
			break;
		if (isdigit(ch) || issymbol(ch))
			*p++ = ch;
	}
	*p = '\0';

}

bool issymbol(char symbol)
{
	switch (symbol)
	{
	case '(':
	case ')':
	case '+':
	case '-':
	case '*':
	case '/': return true;
	default:  return false;
	}
}
