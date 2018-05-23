#include "stdio.h"
#include "stdlib.h"
#include "string.h"
#include "train_info.h"
#include "order_info.h"

void input_train_info()
{
	FILE *fp;
	char str0, *str_link = "|", *str_enter = "\n", train_num[10], train_route[100], train_node[100], train_info[1000];
	TNODE tnode_start;
	int i;
	if (!fopen_s(&fp, "traininfo.txt", "a+"))
	{
		printf("是否继续输入车次信息，结束请输入‘#’，继续请输入‘/’：\n");
		_flushall();
		while (getchar() != '#')
		{
			_flushall();
			printf("请输入车次：");
			gets_s(train_num);
			strcpy_s(train_info, train_num);
			strcat_s(train_info, sizeof(train_info), str_link);
			//输入该次列车某条线路信息
			printf("是否继续输入该车次经过站信息，结束请输入‘#’，继续请输入‘/’：\n");
			while (getchar() != '#')
			{
				_flushall();
				printf("请输入车到站详细信息：\n");
				printf("输入格式为”当前站 到达时间 离开时间 停留时间 此站到下一站的时间@座位类型@座位价格@此座位类型原有总座位@此座位类型余票数@“\n");
				printf("例如：”北京 1 2 3 60@1@100@50@14@2@120@50@25@“\n");
				printf("请注意可以依照上面的格式输入当前站的多种座位类型的座位信息\n");
				gets_s(train_node);
				strcat_s(train_info, sizeof(train_info), train_node);
				strcat_s(train_info, sizeof(train_info), str_link);
				_flushall();
				printf("是否继续输入该车次经过站信息，结束请输入‘#’，继续请输入‘/’：\n");
			}
			strcat_s(train_info, sizeof(train_info), str_enter);
			fprintf(fp, "%s", train_info);
			_flushall();
			printf("是否继续输入车次信息，结束请输入‘#’，继续请输入‘/’：\n");
		}
	}
	fclose(fp);

}

void input_traininfo_fast()
{
	//生成测试输入的文件
	FILE * fp1;
	char str1;
	if (fopen_s(&fp1, "testfile.txt", "w"))
	{
		printf("file cannot be opened\n");
		exit(1);
	}
	printf("快速输入一个车次的所有信息：\n");
	printf("输入格式为”车次|当前站 到达时间 离开时间 停留时间 此站到下一站的时间@座位类型@座位价格@此座位类型原有总座位@此座位类型余票数@...|当前站...@|“\n");
	printf("例如：”G1|北京 1 2 3 60@1@100@50@14@2@120@50@25@|桂林 2 3 4 60@1@20@30@10@|“\n");
	printf("请注意可以依照上面的格式输入多个到达站以及当前站的多种座位类型的座位信息\n");
	while ((str1 = fgetc(stdin)) != '#')
		fputc(str1, fp1);
	fclose(fp1);
}

void read_traininfo(TINFO *tinfo)
{
	FILE *fp;
	TNODE *p, *ptemp;
	TINFO *t, *ttemp = tinfo;
	SEAT *q, *qtemp;
	int i = 0, m = 0, n = 0, len;
	char str, str0, buf[MAX_LINE], tnum[10];
	if (fopen_s(&fp, "traininfo.txt", "r"))
	{
		printf("file cannot be opened\n");
		exit(1);
	}
	while (fgets(buf, MAX_LINE, fp) != NULL)
	{
		i = 0;
		len = strlen(buf);
		buf[len - 1] = '\0';  /*去掉换行符*/
		//printf("%s %d \n", buf, len - 1);
		Init_tinfo(t);
		n = 0;
		while (1)
		{
			if (buf[i] == '|')
			{
				t->train_num[n] = '\0';
				tnum[n] = '\0';
				i++;
				break;
			}
			t->train_num[n] = buf[i];
			tnum[n] = buf[i];
			n++;
			i++;
		}
		ptemp = t->tnode;
		while (i < strlen(buf))
		{
			m = 0;
			Init_tnode(p);
			strcpy_s(p->train_num, sizeof(tnum), tnum);
			switch (m)
			{
			case 0:
			{
					  int n = 0;
					  while (1)
					  {
						  if (buf[i] == ' ')
						  {
							  p->current_station[n] = '\0';
							  i++;
							  break;
						  }
						  p->current_station[n] = buf[i];
						  n++;
						  i++;
					  }
					  m++;
			}
			case 1:
			{
					  int n = 0;
					  while (1)
					  {

						  if (buf[i] == ' ')
						  {
							  p->arrive_time[n] = '\0';
							  i++;
							  break;
						  }
						  p->arrive_time[n] = buf[i];
						  n++;
						  i++;
					  }
					  m++;

			}
			case 2:
			{
					  int n = 0;
					  while (1)
					  {

						  if (buf[i] == ' ')
						  {
							  p->leave_time[n] = '\0';
							  i++;
							  break;
						  }
						  p->leave_time[n] = buf[i];
						  n++;
						  i++;
					  }
					  m++;
			}
			case 3:
			{
					  int n = 0;
					  while (1)
					  {

						  if (buf[i] == ' ')
						  {
							  p->stay_time[n] = '\0';
							  i++;
							  break;
						  }
						  p->stay_time[n] = buf[i];
						  n++;
						  i++;
					  }
					  m++;
			}
			case 4:
			{
					  int n = 0;
					  while (1)
					  {

						  if (buf[i] == '@')
						  {
							  p->last_time[n] = '\0';
							  break;
						  }
						  p->last_time[n] = buf[i];
						  n++;
						  i++;
					  }
					  m++;
			}
			case 5:
			{
					  qtemp = p->seat;
					  while (buf[i] == '@'&&buf[i + 1] != '|')
					  {
						  i++;
						  Init_seat(q);
						  n = 0;
						  while (1)
						  {
							  if (buf[i] == '@')
							  {
								  q->seat_type[n] = '\0';
								  i++;
								  break;
							  }
							  q->seat_type[n] = buf[i];
							  n++;
							  i++;
						  }
						  n = 0;
						  while (1)
						  {
							  if (buf[i] == '@')
							  {
								  q->seat_price[n] = '\0';
								  i++;
								  break;
							  }
							  q->seat_price[n] = buf[i];
							  n++;
							  i++;
						  }
						  n = 0;
						  while (1)
						  {
							  if (buf[i] == '@')
							  {
								  q->ticket_total[n] = '\0';
								  i++;
								  break;
							  }
							  q->ticket_total[n] = buf[i];
							  n++;
							  i++;
						  }
						  n = 0;
						  while (1)
						  {
							  if (buf[i] == '@')
							  {
								  q->ticket_left[n] = '\0';
								  break;
							  }
							  q->ticket_left[n] = buf[i];
							  n++;
							  i++;
						  }
						  q->next = qtemp->next;
						  qtemp->next = q;
						  qtemp = qtemp->next;
					  }
					  qtemp = NULL;

			}
				//printf("%s,%s,%s", p->seat->next->next->seat_type, p->seat->next->next->seat_price, p->seat->next->next->ticket_left);
				p->next = ptemp->next;
				ptemp->next = p;
				ptemp = ptemp->next;
				i += 2;
			}
		}
		ptemp = NULL;
		t->next = ttemp->next;
		ttemp->next = t;
		ttemp = ttemp->next;
	}
	ttemp = NULL;
	//printf("%s,%s,%s", tinfo->next->tnode->next->current_station, tinfo->next->next->train_num, tinfo->next->next->tnode->next->seat->next->seat_type);
	fclose(fp);


}

Status Locate_train_route(TINFO *head, QUEUE &Q, char *key_s, char *key_d)
{
	TINFO *p = head;
	TNODE *q = p->tnode->next, *temp = NULL;
	int f = 0;
	while (q)
	{
		if ((strcmp(key_d, q->current_station) == 0) && (temp != NULL) && (strcmp(key_s, temp->current_station) == 0))
		{
			EnQueue(Q, q);
			f = 0;
		}
		if (f == 1)
			EnQueue(Q, q);
		if (strcmp(key_s, q->current_station) == 0)
		{
			EnQueue(Q, q);
			f = 1;
		}
		if (!QueueEmpty(Q))
			GetFront(Q, temp);

		q = q->next;
	}
	if (f == 1 && !(QueueEmpty(Q)))
	{
		if (EmptyQueue(Q))
			return OK;
		else
			return ERROR;
	}
	return OK;
}

int *is_ticket(TNODE *tnode)
{
	SEAT *s;
	int temp, stype, left_ticket[10][2] = { 0 }, i = 0, count = 0, f = 0, *t;
	static int ticket_type[10] = { 0 };
	t = ticket_type;
	TNODE *q = tnode;
	QUEUE Q;
	InitQueue(Q);
	s = q->seat;
	s = s->next;
	while (q)
	{
		while (s)
		{
			//定义一个队列，先逐一进队列，检查余票是否都够，够的话，输出队列
			temp = atoi(s->ticket_left);
			stype = atoi(s->seat_type);
			if (left_ticket[stype][0] == 0)
			{
				left_ticket[stype][0] = temp;
				left_ticket[stype][1]++;
			}

			else if (temp < left_ticket[stype][0] && temp >= 0)
			{
				left_ticket[stype][0] = temp;
				left_ticket[stype][1]++;
			}
			else
				left_ticket[stype][1]++;
			s = s->next;
		}
		count++;
		EnQueue(Q, q);
		q = q->next;
		if (q)
			s = q->seat->next;
	}
	printf("途径站：\n");
	do
	{
		if (DeQueue(Q, q))
			puts(q->current_station);
		else
			break;

	} while (q);
	printf("剩下票(座位种类--余票数量)：\n");
	for (stype = 1; stype <= 10; stype++)
	{
		if (left_ticket[stype])
		{
			if (left_ticket[stype][0] > 0 && left_ticket[stype][1] == count)
			{
				ticket_type[i++] = stype;
				f = 1;
				printf("%d--%d\n", stype, left_ticket[stype][0]);
			}

		}
		else continue;

	}
	if (!f)
		printf("没有剩余票！\n");
	return t;

}

QUEUE search_traininfo(TINFO *tinfo)
{
	QUEUE Q, Q1, result;
	TINFO *p, t;
	TNODE *q, *temp;
	SEAT *s;
	char key[10], key_s[10], key_d[10], str;
	int left_ticket[10][1] = { 0 }, i = 0, j = 0, stype, iszero = 0, m, *ticket_type;
	InitQueue(result);
	printf("\n查询方式：\n1--按照车次进行查询\n2--按照路线进行查询\n结束查询请输入‘#’\n");
	while (1)
	{
		_flushall();
		printf("请选择查询方式：");
		scanf_s("%d", &m);
		switch (m)
		{
		case 1:
		{
				  //根据车次查询
				  //根据输入的车次，进行查询，定位至车次所在位置前一个
				  /*if (!QueueEmpty)
				  EmptyQueue(result);*/
				  _flushall();
				  printf("请输入需要查询的车次号：\n");
				  gets_s(key, sizeof(key));
				  p = Locate_train_num(tinfo, key);
				  // EnQueue(result, p->next->tnode->next);
				  if (p->next)
				  {
					  p = p->next;
					  q = p->tnode;
					  q = q->next;
					  is_ticket(q);

				  }
				  //如果车次不存在，退出查询
				  else
					  printf("没有查询到车次为%s的列车\n", key);

				  break;

		}
		case 2:
		{
				  //根据路线查询
				  int count = 0;
				  /* if (!QueueEmpty)
				  EmptyQueue(result);*/
				  printf("请输入要查询路线的起点：");
				  _flushall();
				  gets_s(key_s, sizeof(key_s));
				  printf("请输入要查询路线的终点：");
				  _flushall();
				  gets_s(key_d, sizeof(key_d));
				  p = tinfo->next;
				  InitQueue(Q);
				  do
				  {
					  Locate_train_route(p, Q, key_s, key_d);
					  if (QueueEmpty(Q))
						  p = p->next;
					  else
					  {
						  count++;
						  printf("车次号为：");
						  DeQueue(Q, q);
						  puts(q->train_num);
						  // while (!QueueEmpty(Q))
						  //{
						  // DeQueue(Q, q);
						  ticket_type = is_ticket(q);
						  if (*ticket_type>0)
						  {
							  EnQueue(result, q);
						  }
						  EmptyQueue(Q);
						  break;
						  //  }
						  p = p->next;

					  }

				  } while (p);
				  if (count)
					  printf("共查询到%d个结果\n", count);
				  else
					  printf("未查询到结果！\n");
		}
		}
		printf("结束查询请输入‘#’,按其它任意键继续查询\n");
		if (getchar() == '#')
			break;

	}
	return result;

}

void write_traininfo(TINFO *&tinfo)
{
	FILE *fp;
	char str0, *str_link_t = "|", *str_enter = "\n", *str_link_c = " ", *str_link_s = "@", train_num[10], train_node[100], train_info[1000];
	TINFO *p = tinfo->next;
	TNODE *q;
	SEAT *s;
	if (fopen_s(&fp, "traininfo.txt", "w"))
	{
		printf("file cannot be opened\n");
		exit(1);
	}
	else
	{
		while (p)
		{
			strcpy_s(train_info, p->train_num);
			strcat_s(train_info, sizeof(train_info), str_link_t);
			//输入该次列车某条线路信息
			//G1|北京 1 2 3 60@1@100@50@25@2@120@50@25@|桂林 2 3 4 60@1@20@30@15@|
			//当前站 到达时间 离开时间 停留时间 从此站到下一站经过时间
			//@座位类型@座位价格@总票数@余票数
			q = p->tnode->next;
			while (q)
			{

				strcpy_s(train_node, sizeof(train_node), q->current_station);
				strcat_s(train_node, sizeof(train_node), str_link_c);
				strcat_s(train_node, sizeof(train_node), q->arrive_time);
				strcat_s(train_node, sizeof(train_node), str_link_c);
				strcat_s(train_node, sizeof(train_node), q->leave_time);
				strcat_s(train_node, sizeof(train_node), str_link_c);
				strcat_s(train_node, sizeof(train_node), q->stay_time);
				strcat_s(train_node, sizeof(train_node), str_link_c);
				strcat_s(train_node, sizeof(train_node), q->last_time);
				s = q->seat->next;
				strcat_s(train_node, sizeof(train_node), str_link_s);
				while (s)
				{
					strcat_s(train_node, sizeof(train_node), s->seat_type);
					strcat_s(train_node, sizeof(train_node), str_link_s);
					strcat_s(train_node, sizeof(train_node), s->seat_price);
					strcat_s(train_node, sizeof(train_node), str_link_s);
					strcat_s(train_node, sizeof(train_node), s->ticket_total);
					strcat_s(train_node, sizeof(train_node), str_link_s);
					strcat_s(train_node, sizeof(train_node), s->ticket_left);
					strcat_s(train_node, sizeof(train_node), str_link_s);
					s = s->next;
				}
				strcat_s(train_info, sizeof(train_info), train_node);
				strcat_s(train_info, sizeof(train_info), str_link_t);
				q = q->next;
			}
			strcat_s(train_info, sizeof(train_info), str_enter);
			fprintf(fp, "%s", train_info);
			p = p->next;
		}
	}
	fclose(fp);

}

void del_traininfo(TINFO *&tinfo)
{
	char key[10], train_num[10];
	TINFO *temp, *del;
	while (1)
	{
		printf("请输入要删除的列车车次号：");
		_flushall();
		gets_s(key, sizeof(key));

		temp = Locate_train_num(tinfo, key);
		if (temp->next)
		{
			strcpy_s(train_num, sizeof(train_num), temp->next->train_num);
			if (ListDelete(temp, del))
			{
				write_traininfo(tinfo);
				printf("列车号为%s的列车记录删除成功！\n", train_num);
			}
			else
				printf("列车号为%s的列车记录删除失败！\n", train_num);
		}
		else
			printf("未查找到目标列车号，请检查输入！\n");
		printf("结束删除请输入‘#’,继续删除请输入‘/’\n");
		if (getchar() == '#')
			break;

	}
}

void read_all_traininfo(TINFO *tinfo)
{
	FILE *fp;
	char buf[MAX_LINE];
	int i, len;
	if (fopen_s(&fp, "traininfo.txt", "r"))
	{
		printf("file cannot be opened\n");
		exit(1);
	}
	while (fgets(buf, MAX_LINE, fp) != NULL)
	{
		i = 0;
		len = strlen(buf);
		buf[len - 1] = '\0';  /*去掉换行符*/
		printf("%s %d \n", buf, len - 1);
	}
	fclose(fp);
}

void book_ticket(TINFO *tinfo, OINFO *&oinfo)
{
	FILE *fp;
	OINFO *o;
	TNODE *temp, *p;
	QUEUE Q, Q1;
	SEAT *s;
	int t;
	char buf[MAX_LINE], orderinfo[1000], *str_link = "|", *str_enter = "\n", templine[2];
	int line = 0, len, *ticket_type, i = 0, isticket = 0, price = 0;
	Init_order(o);
	InitQueue(Q1);
	//读取orderinfo.txt，获得最前的订单编号，关闭文档
	o->order_id[0] = '1';
	o->order_id[1] = '\0';
	if (fopen_s(&fp, "orderinfo.txt", "r"))
	{
		printf("file cannot be opened\n");
		//exit(1);
	}
	else
	{
		templine[0] = '0';
		while (fgets(buf, MAX_LINE, fp) != NULL)
			templine[0] = buf[0];
		line = atoi(templine);
		line++; //新建订单记录的编号
		_itoa_s(line, o->order_id, 10);
		fclose(fp);
	}
	//先输入用户ID，用户名
	printf("请输入您的用户id：");
	_flushall();
	gets_s(o->passenger_id, sizeof(o->passenger_name));
	printf("请输入您的用户名：");
	gets_s(o->passenger_name, sizeof(o->passenger_name));
	//根据车次或路线对车次余票进行查询
	printf("请根据提示信息进行余票查询（注意确认订票之前请务必进行对路线的查询）：");
	Q = search_traininfo(tinfo);
	printf("请输入最终确定车次：");
	_flushall();
	gets_s(o->train_num, sizeof(o->train_num));
	printf("请输入最终确定起始站：");
	_flushall();
	gets_s(o->station_s, sizeof(o->station_s));
	printf("请输入最终确定终点站：");
	_flushall();
	gets_s(o->station_d, sizeof(o->station_d));
	printf("请输入座位种类：");
	_flushall();
	gets_s(o->ticket_type, sizeof(o->ticket_type));
	if (QueueEmpty(Q))
		printf("请检查输入，以上起点站到终点站没有余票！");
	while (!QueueEmpty(Q))
	{
		DeQueue(Q, temp);
		if (strcmp(temp->current_station, o->station_s) == 0 && strcmp(temp->train_num, o->train_num) == 0)
		{
			p = temp;
			while (strcmp(p->current_station, o->station_d) != 0)
			{
				EnQueue(Q1, p);
				p = p->next;
			}
			EnQueue(Q1, p);
			GetFront(Q1, p);
			ticket_type = is_ticket(p);
			int a, b;
			do
			{
				b = *(ticket_type + i);
				a = atoi(o->ticket_type);
				if (b == a)
				{
					//有票
					isticket = 1;
					break;
				}
				i++;

			} while (b > 0);
			if (isticket == 0)
			{
				//没票，提示信息
				printf("此次列车此条路线无剩余票，请检查输入！\n");
			}
			if (isticket == 1)
			{
				//拼接信息，写入文档,同时火车信息文件中对应站点票减少1
				strcpy_s(orderinfo, sizeof(orderinfo), o->order_id);
				strcat_s(orderinfo, sizeof(orderinfo), str_link);
				o->state[0] = '1';
				o->state[1] = '\0';
				strcat_s(orderinfo, sizeof(orderinfo), o->state);
				strcat_s(orderinfo, sizeof(orderinfo), str_link);
				strcat_s(orderinfo, sizeof(orderinfo), o->passenger_id);
				strcat_s(orderinfo, sizeof(orderinfo), str_link);
				strcat_s(orderinfo, sizeof(orderinfo), o->passenger_name);
				strcat_s(orderinfo, sizeof(orderinfo), str_link);
				strcat_s(orderinfo, sizeof(orderinfo), o->train_num);
				strcat_s(orderinfo, sizeof(orderinfo), str_link);
				strcat_s(orderinfo, sizeof(orderinfo), o->station_s);
				strcat_s(orderinfo, sizeof(orderinfo), str_link);
				strcat_s(orderinfo, sizeof(orderinfo), o->station_d);
				strcat_s(orderinfo, sizeof(orderinfo), str_link);
				strcat_s(orderinfo, sizeof(orderinfo), o->ticket_type);
				strcat_s(orderinfo, sizeof(orderinfo), str_link);
				if (fopen_s(&fp, "orderinfo.txt", "a+"))
				{
					printf("file cannot be opened\n");
					//exit(1);
				}
				else
				{
					fprintf(fp, "%s", orderinfo);
					//GetFront(Q1, p);
					do
					{
						DeQueue(Q1, p);
						s = p->seat;
						while (s)
						{
							if (strcmp(o->ticket_type, s->seat_type) == 0)
							{
								t = atoi(s->ticket_left);
								t--;
								_itoa_s(t, s->ticket_left, 10);
								price += atoi(s->seat_price);
								break;
							}
							s = s->next;
						}
					} while (!QueueEmpty(Q1));
					_itoa_s(price, o->ticket_price, 10);
					strcpy_s(orderinfo, sizeof(orderinfo), o->ticket_price);
					strcat_s(orderinfo, sizeof(orderinfo), str_link);
					strcat_s(orderinfo, sizeof(orderinfo), str_enter);
					fprintf(fp, "%s", orderinfo);
					fclose(fp);
					write_traininfo(tinfo);
					printf("成功订票！");
					printf("订票信息为（订单编号-用户id-用户名-起始站-终点站-车票类型-车票价格）：%s-%s-%s-%s-%s-%s-%s\n", o->order_id, o->passenger_id, o->passenger_name, o->station_s, o->station_d, o->ticket_type, o->ticket_price);
				}
			}
		}

	}


}

void write_orderinfo(OINFO *oinfo)
{
	FILE *fp;
	OINFO *o = oinfo->next;
	char orderinfo[1000], *str_link = "|", *str_enter = "\n";
	if (fopen_s(&fp, "orderinfo.txt", "w"))
	{
		printf("file cannot be opened\n");
		//exit(1);
	}
	else
	{
		while (o)
		{
			strcpy_s(orderinfo, sizeof(orderinfo), o->order_id);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), o->state);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), o->passenger_id);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), o->passenger_name);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), o->train_num);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), o->station_s);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), o->station_d);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), o->ticket_type);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), o->ticket_price);
			strcat_s(orderinfo, sizeof(orderinfo), str_link);
			strcat_s(orderinfo, sizeof(orderinfo), str_enter);
			fprintf(fp, "%s", orderinfo);
			o = o->next;
		}

	}
	fclose(fp);
}

void read_orderinfo(OINFO *&oinfo)
{
	FILE *fp;
	OINFO *o = oinfo, *otemp;
	int i = 0, m = 0, n = 0, len;
	char str, str0, buf[MAX_LINE], tnum[10];
	if (fopen_s(&fp, "orderinfo.txt", "r"))
	{
		printf("file cannot be opened\n");
		exit(1);
	}
	else
	{
		while (fgets(buf, MAX_LINE, fp) != NULL)
		{
			i = 0;
			len = strlen(buf);
			buf[len - 1] = '\0';  /*去掉换行符*/
			//printf("%s %d \n", buf, len - 1);
			n = 0;
			otemp = (OINFO *)malloc(sizeof(OINFO));
			//6 | 1 | 450325199706170684 | 刘源 | G1 | 北京 | 桂林 | 1 | 120 |
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->order_id[n] = '\0';
					i++;
					break;
				}
				otemp->order_id[n] = buf[i];
				n++;
				i++;
			}
			n = 0;
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->state[n] = '\0';
					i++;
					break;
				}
				otemp->state[n] = buf[i];
				n++;
				i++;
			}
			n = 0;
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->passenger_id[n] = '\0';
					i++;
					break;
				}
				otemp->passenger_id[n] = buf[i];
				n++;
				i++;
			}
			n = 0;
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->passenger_name[n] = '\0';
					i++;
					break;
				}
				otemp->passenger_name[n] = buf[i];
				n++;
				i++;
			}
			n = 0;
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->train_num[n] = '\0';
					i++;
					break;
				}
				otemp->train_num[n] = buf[i];
				n++;
				i++;
			}
			n = 0;
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->station_s[n] = '\0';
					i++;
					break;
				}
				otemp->station_s[n] = buf[i];
				n++;
				i++;
			}
			n = 0;
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->station_d[n] = '\0';
					i++;
					break;
				}
				otemp->station_d[n] = buf[i];
				n++;
				i++;
			}
			n = 0;
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->ticket_type[n] = '\0';
					i++;
					break;
				}
				otemp->ticket_type[n] = buf[i];
				n++;
				i++;
			}
			n = 0;
			while (1)
			{
				if (buf[i] == '|')
				{
					otemp->ticket_price[n] = '\0';
					i++;
					break;
				}
				otemp->ticket_price[n] = buf[i];
				n++;
				i++;
			}
			otemp->next = o->next;
			o->next = otemp;
			o = o->next;
		}
		o = NULL;

	}
	fclose(fp);

}

void read_all_orderinfo(OINFO *oinfo)
{
	FILE *fp;
	char buf[MAX_LINE];
	int i, len;
	if (fopen_s(&fp, "orderinfo.txt", "r"))
	{
		printf("file cannot be opened\n");
		exit(1);
	}
	while (fgets(buf, MAX_LINE, fp) != NULL)
	{
		i = 0;
		len = strlen(buf);
		buf[len - 1] = '\0';  /*去掉换行符*/
		printf("%s %d \n", buf, len - 1);
	}
	fclose(fp);
}

void search_order(OINFO *oinfo)
{
	OINFO *o;
	char oid[10], pid[30], pname[20];
	OQUEUE Q;
	int f = 0, m;
	printf("1--通过订单编号进行查询\n2--通过用户id，用户名进行查询\n");
	while (1)
	{
		printf("请选择查询订单的方式：");
		scanf_s("%d", &m);
		switch (m)
		{
		case 1:
		{
				  //通过订单编号进行查询
				  printf("请输入要进行查询的订单编号：");
				  _flushall();
				  gets_s(oid, sizeof(oid));
				  o = Locate_order_id(oinfo, oid);
				  if (o->next)
				  {
					  o = o->next;
					  printf("查询到的结果为：\n");
					  printf("%s-%s-%s-%s-%s-%s-%s-%s\n", o->order_id, o->state, o->passenger_id, o->passenger_name, o->station_s, o->station_d, o->ticket_type, o->ticket_price);
				  }
				  else
					  printf("没有查询到结果！\n");
				  break;
		}
		case 2:
		{

				  //通过用户名，用户id查看用户的订单信息
				  f = 0;
				  printf("请输入用户id：");
				  _flushall();
				  gets_s(pid, sizeof(pid));
				  printf("请输入用户名：");
				  _flushall();
				  gets_s(pname, sizeof(pname));
				  Q = Locate_pid_pname(oinfo, pid, pname);
				  printf("查询到的结果为：\n");
				  while (!OQueueEmpty(Q))
				  {
					  f = 1;
					  ODeQueue(Q, o);
					  printf("订单编号-订单状态-用户id-用户名-起始站-终点站-车票类型-车票价格\n%s-%s-%s-%s-%s-%s-%s-%s\n", o->order_id, o->state, o->passenger_id, o->passenger_name, o->station_s, o->station_d, o->ticket_type, o->ticket_price);
				  }
				  if (!f)
					  printf("未查询到结果！\n");
				  break;
		}
		default:
		{
				   printf("\n");
				   break;
		}

		}
		printf("继续查询请输入‘/’，退出查询请输入‘#’");
		if (getchar() == '#')
			break;
	}
}

void del_orderinfo(OINFO *&oinfo)
{
	char del[5], *s0 = "0";
	OINFO *o, *temp;
	int i;
	//根据订单编号查找
	printf("是否先对需要删除的订单进行查询，是请输入‘1’，不是请输入‘0’\n");
	scanf_s("%d", &i);
	if (i)
		search_order(oinfo);
	printf("请输入需要删除的订单记录编号：");
	_flushall();
	gets_s(del, sizeof(del));
	o = Locate_order_id(oinfo, del);
	//输入订单编号，将此订单记录删除
	if (o->next&&strcmp(o->next->state, s0) == 0)
	{
		OListDelete(o, temp);
		write_orderinfo(oinfo);
		printf("订单号为%s的订单记录删除成功！\n", o->next->order_id);
	}
	else
	{
		printf("不存在此退单号，请检查输入！\n");
	}
}

void del_order_auto(OINFO *oinfo)
{
	OINFO *o = oinfo, *temp;
	char *s0 = "0";
	while (o->next)
	{
		if (strcmp(o->next->state, s0) == 0)
		{
			OListDelete(o, temp);
		}
		o = o->next;
	}
	write_orderinfo(oinfo);
	printf("自动删除完成\n");
}

void cancel_ticket(TINFO *tinfo, OINFO *&oinfo)
{
	char oid[5], *s1 = "1", *s0 = "0";
	OINFO *o;
	QUEUE Q;
	TNODE *t;
	SEAT *s;
	int temp;
	InitQueue(Q);
	//确认订票信息是否存在，票是否已经被退
	search_order(oinfo);
	while (1)
	{
		printf("请输入要退票的订单编号：");
		_flushall();
		gets_s(oid, sizeof(oid));
		o = Locate_order_id(oinfo, oid);
		if (o->next)
		{
			o = o->next;
			if (strcmp(o->state, s0) == 0)
			{
				printf("请注意该票已经被退订！\n");
			}
			if (strcmp(o->state, s1) == 0)
			{
				//确认存在订票信息，票未被退，将订票信息文档中state由1改为0，traninfo中所有经过站的余票加一
				strcpy_s(o->state, sizeof(o->state), s0);
				Locate_train_route(tinfo->next, Q, o->station_s, o->station_d);
				while (!QueueEmpty(Q))
				{
					DeQueue(Q, t);
					s = t->seat->next;
					while (s)
					{
						if (strcmp(s->seat_type, o->ticket_type) == 0)
						{
							temp = atoi(s->ticket_left);
							temp++;
							_itoa_s(temp, s->ticket_left, 10);
							break;
						}
						s = s->next;
					}
				}
				write_traininfo(tinfo);
				write_orderinfo(oinfo);
				printf("退票成功！\n");
				printf("退票信息为(订单编号-用户id-用户名-起始站-终点站-车票类型-车票价格)：\n%s-%s-%s-%s-%s-%s-%s\n", o->order_id, o->passenger_id, o->passenger_name, o->station_s, o->station_d, o->ticket_type, o->ticket_price);
			}
		}
		else
			printf("请注意此订单不存在！\n");
		printf("退出删除订单记录请输入‘#’，按其他任意键继续：");
		if (getchar() == '#')
			break;

	}


}

void main()
{
	int m = 0, n = 0, p = 0;
	TINFO *tinfo;
	OINFO *oinfo;
	Init_tinfo(tinfo);
	Init_order(oinfo);
	read_traininfo(tinfo);
	read_orderinfo(oinfo);
	while (1)
	{
		printf("请选择进入的系统：\n1--管理员系统\n2--乘客系统\n");
		scanf_s("%d", &m);
		switch (m)
		{
		case 1:
		{
				  while (1)
				  {
					  printf("请选择操作：\n1--管理列车信息\n2--管理订单信息\n");
					  scanf_s("%d", &n);
					  switch (n)
					  {

					  case 1:
					  {
								while (1)
								{
									printf("请选择：\n1--添加列车信息\n2--快速添加列车信息\n3--删除列车信息\n4--查看所有列车信息\n");
									scanf_s("%d", &p);
									switch (p)
									{
									case 1:
									{
											  input_train_info();
											  DestroyList(tinfo);
											  read_traininfo(tinfo);
											  break;
									}
									case 2:
									{
											  input_traininfo_fast();
											  DestroyList(tinfo);
											  read_traininfo(tinfo);
											  break;
									}
									case 3:
									{
											  del_traininfo(tinfo);
											  break;
									}
									case 4:
									{
											  read_all_traininfo(tinfo);
											  break;
									}
									default:
										break;
									}
									printf("退出管理列车信息输入‘#’，按其他任意键继续：");
									_flushall();
									if (getchar() == '#')
										break;

								}
								break;

					  }
					  case 2:
					  {
								while (1)
								{
									printf("请选择：\n1--选择删除已退订单的记录\n2--快速自动删除所有已退订单记录\n3--查看所有订单信息\n");
									scanf_s("%d", &p);
									switch (p)
									{
									case 1:
									{
											  del_orderinfo(oinfo);
											  break;
									}
									case 2:
									{
											  del_order_auto(oinfo);
											  break;
									}
									case 3:
									{
											  read_all_orderinfo(oinfo);
											  break;
									}
									default:
										break;
									}
									printf("退出管理订单信息输入‘#’，按其他任意键继续：");
									_flushall();
									if (getchar() == '#')
										break;


								}
					  }
					  }
					  printf("退出管理员操作输入‘#’，按任何其他键继续管理员操作：");
					  _flushall();
					  if (getchar() == '#')
						  break;
				  }
				  break;
		}
		case 2:
		{
				  while (1)
				  {
					  printf("请选择操作：\n1--车票查询\n2--车票预定\n3--退票\n4--订票信息查询\n");
					  scanf_s("%d", &n);
					  switch (n)
					  {
					  case 1:
					  {
								search_traininfo(tinfo);
								break;
					  }
					  case 2:
					  {
								book_ticket(tinfo, oinfo);
								ODestroyList(oinfo);
								Init_order(oinfo);
								read_orderinfo(oinfo);
								break;
					  }
					  case 3:
					  {
								cancel_ticket(tinfo, oinfo);
								break;
					  }
					  case 4:
					  {
								ODestroyList(oinfo);
								Init_order(oinfo);
								read_orderinfo(oinfo);
								search_order(oinfo);
								break;
					  }
					  default:
						  break;
					  }
					  printf("退出乘客操作输入‘#’，按任何其他键继续\n");
					  _flushall();
					  if (getchar() == '#')
						  break;

				  }
				  break;

		}
		default:
			break;
		}
		printf("退出火车票管理系统请输入‘#’，按其他任何键继续操作\n");
		_flushall();
		if (getchar() == '#')
			break;

	}


}


