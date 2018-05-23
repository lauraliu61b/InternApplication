#define MAX_LINE 1024
typedef struct seat
{
	char seat_type[10];
	char seat_price[10];
	char ticket_total[10];
	char ticket_left[10];
	struct seat *next;
}SEAT;

typedef struct train_node_info
{
	char train_num[10];
	char current_station[10];
	char stay_time[10];
	char arrive_time[10];
	char leave_time[10];
	char last_time[10];
	SEAT *seat;
	struct train_node_info *next;

}TNODE;

typedef struct train_info
{
	char train_num[10];
	TNODE *tnode;
	struct train_info *next;
}TINFO;

int Init_seat(SEAT *&head)
{
	head = (SEAT *)malloc(sizeof(SEAT));
	if (head == NULL) exit(0);
	head->next = NULL;
	return 1;
}

int Init_tnode(TNODE *&head)
{
	head = (TNODE *)malloc(sizeof(TNODE));
	if (head == NULL) exit(0);
	head->next = NULL;
	if (Init_seat(head->seat))
		return 1;
}

int Init_tinfo(TINFO *&head)
{
	head = (TINFO *)malloc(sizeof(TINFO));
	if (head == NULL) exit(0);
	head->next = NULL;
	if (Init_tnode(head->tnode) && Init_seat(head->tnode->seat))
		return 1;
}



TINFO *Locate_train_num(TINFO *head, char *key) //根据车次查询，定位至目标元素前一个
{
	TINFO *p = head;
	while (p&&p->next && (strcmp(p->next->train_num, key) != 0))
		p = p->next;
	return p;
}

int ListDelete(TINFO *&tinfo, TINFO *&e)
{
	TINFO *temp = tinfo;
	e = tinfo->next;
	temp->next = e->next;
	free(e);
	return 1;
}

void DestroyList(TINFO *&head)
{
	TINFO *p = head->next;
	while (p != NULL)
	{
		head->next = p->next;
		free(p);
		p = head->next;
	}
	free(head);
}

//队列基本操作
typedef TNODE QElemType;
typedef int Status;
#define ERROR 0
#define TRUE 1
#define FALSE 0
#define OK 1

typedef struct lnode
{
	QElemType *data;
	struct lnode *next;
}NODE;
typedef struct queue
{
	NODE *front;
	NODE *rear;
}QUEUE;
Status InitQueue(QUEUE &Q)
{
	Q.front = (NODE *)malloc(sizeof(NODE));
	if (Q.front == NULL) return ERROR;
	Q.rear = Q.front;
	return OK;
}
Status QueueEmpty(QUEUE Q)
{
	if (Q.front == Q.rear) return TRUE;
	else return FALSE;
}
Status EnQueue(QUEUE &Q, QElemType *x)
{
	NODE *s = (NODE *)malloc(sizeof(NODE));
	if (!s) return ERROR;
	s->data = x;
	s->next = NULL;
	Q.rear->next = s;
	Q.rear = s;
	return OK;
}
Status DeQueue(QUEUE &Q, QElemType *&item)
{
	NODE *s;
	if (QueueEmpty(Q)) return ERROR;
	else
	{
		item = Q.front->next->data;
		s = Q.front->next;
		Q.front->next = s->next;
		if (s->next == NULL) Q.rear = Q.front;
		free(s);
		return OK;
	}
}
Status EmptyQueue(QUEUE &Q)
{
	NODE *s;
	QElemType *temp;
	if (QueueEmpty(Q)) return OK;
	else
	{
		while (1)
		{
			if (DeQueue(Q, temp))
				continue;
			else
				break;
		}
	}
	if (QueueEmpty(Q)) return OK;
	else return ERROR;
}
void GetFront(QUEUE Q, QElemType *&item)
{
	if (QueueEmpty(Q)) exit(ERROR);
	else item = Q.front->next->data;
}
