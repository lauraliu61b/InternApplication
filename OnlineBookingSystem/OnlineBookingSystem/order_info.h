typedef struct orderinfo
{
	char order_id[10];
	char passenger_id[30];
	char passenger_name[20];
	char train_num[10];
	char station_s[10];
	char station_d[10];
	char ticket_price[5];
	char ticket_type[5];
	char state[2];
	struct orderinfo *next;

}OINFO;

typedef OINFO OQElemType;

typedef struct onode
{
	OQElemType *data;
	struct onode *next;
}ONODE;
typedef struct oqueue
{
	ONODE *front;
	ONODE *rear;
}OQUEUE;
Status OInitQueue(OQUEUE &Q)
{
	Q.front = (ONODE *)malloc(sizeof(ONODE));
	if (Q.front == NULL) return ERROR;
	Q.rear = Q.front;
	return OK;
}
Status OQueueEmpty(OQUEUE Q)
{
	if (Q.front == Q.rear) return TRUE;
	else return FALSE;
}
Status OEnQueue(OQUEUE &Q, OQElemType *x)
{
	ONODE *s = (ONODE *)malloc(sizeof(ONODE));
	if (!s) return ERROR;
	s->data = x;
	s->next = NULL;
	Q.rear->next = s;
	Q.rear = s;
	return OK;
}
Status ODeQueue(OQUEUE &Q, OQElemType *&item)
{
	ONODE *s;
	if (OQueueEmpty(Q)) return ERROR;
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
Status OEmptyQueue(OQUEUE &Q)
{
	ONODE *s;
	OQElemType *temp;
	if (OQueueEmpty(Q)) return OK;
	else
	{
		while (1)
		{
			if (ODeQueue(Q, temp))
				continue;
			else
				break;
		}
	}
	if (OQueueEmpty(Q)) return OK;
	else return ERROR;
}
void OGetFront(OQUEUE Q, OQElemType *&item)
{
	if (OQueueEmpty(Q)) exit(ERROR);
	else item = Q.front->next->data;
}

//链表操作
int Init_order(OINFO *&head)
{
	head = (OINFO *)malloc(sizeof(OINFO));
	if (head == NULL) exit(0);
	head->next = NULL;
	return 1;
}

OINFO *Locate_order_id(OINFO *head, char *key) //根据订单编号查询，定位至目标元素前一个
{
	OINFO *p = head;
	while (p&&p->next && (strcmp(p->next->order_id, key) != 0))
		p = p->next;
	return p;
}

OQUEUE Locate_pid_pname(OINFO *head, char *pid, char *pname) //根据用户id，用户名查询，定位至目标元素前一个
{
	OINFO *p = head;
	OQUEUE Q;
	OInitQueue(Q);
	//while (p&&p->next && (strcmp(p->next->passenger_id, pid) != 0)&&(strcmp(p->next->passenger_name,pname)!=0))
	//p = p->next;
	while (p->next)
	{
		if ((strcmp(p->next->passenger_id, pid) == 0) && (strcmp(p->next->passenger_name, pname) == 0))
			OEnQueue(Q, p->next);
		p = p->next;
	}
	return Q;
}

int OListDelete(OINFO *&oinfo, OINFO *&e)
{
	OINFO *temp = oinfo;
	e = oinfo->next;
	temp->next = e->next;
	free(e);
	return 1;
}

void ODestroyList(OINFO *&head)
{
	OINFO *p = head->next;
	while (p != NULL)
	{
		head->next = p->next;
		free(p);
		p = head->next;
	}
	free(head);
}
