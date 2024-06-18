import threading
import time
import random
import collections
import logging
import argparse

logging.basicConfig(level=logging.DEBUG, format='%(message)s')

class Cliente(threading.Thread):
    def __init__(self, nome, bartender):
        super().__init__()
        self.nome = nome
        self.bartender = bartender
        self.e_esperar = threading.Event()
        self.beber = True

    def continuar(self):
        self.e_esperar.set()

    def faz_pedido(self):
        self.beber = True
        self.bartender.pedir(self)

    def espera_pedido(self):
        self.e_esperar.wait()
        self.e_esperar.clear()

    def recebe_pedido(self):
        tempo = random.randint(1, 3)
        time.sleep(tempo)
        logging.info(f"Cliente {self.nome} bebendo")

    def consome_pedido(self):
        tempo = random.randint(1, 3)
        time.sleep(tempo)
        logging.info(f"Cliente {self.nome} bebeu")
        self.bartender.espera_beberem()

    def run(self):
        while not self.bartender.fechou():
            self.faz_pedido()
            if self.beber:
                self.espera_pedido()
                self.recebe_pedido()
                self.consome_pedido()

class Garcom(threading.Thread):
    def __init__(self, max_cli, nome, bartender):
        super().__init__()
        self.nome = nome
        self.bartender = bartender
        self.max_cli = max_cli
        self.anotados = []

    def recebe_max_pedidos(self):
        while len(self.anotados) < self.max_cli:
            cliente_atual = self.bartender.anotar_pedido(self)
            if cliente_atual is not None:
                if cliente_atual.beber:
                    self.anotados.append(cliente_atual)
                    logging.info(f"Garcom {self.nome} recebeu o pedido do Cliente {cliente_atual.nome}")
                else:
                    cliente_atual.continuar()
            else:
                break

    def registra_pedidos(self):
        if self.anotados:
            tempo = random.randint(1, 3)
            time.sleep(tempo)
            logging.info(f"Garcom {self.nome} registrou os pedidos dos clientes {[cliente.nome for cliente in self.anotados]}")

    def entrega_pedidos(self):
        for cliente in self.anotados:
            cliente.continuar()
            logging.info(f"Garcom {self.nome} entregou para o Cliente {cliente.nome}")
        self.anotados.clear()

    def run(self):
        while not self.bartender.fechou():
            self.recebe_max_pedidos()
            self.registra_pedidos()
            self.entrega_pedidos()

class Bartender:
    def __init__(self, n_clientes, tot_rodada):
        self.buff_quer = collections.deque(maxlen=1)
        self.buff_n_quer = collections.deque(maxlen=1)
        self.lock = threading.Condition()
        self.vazio = threading.Semaphore(1)
        self.cheio = threading.Semaphore(0)

        self.n_clientes = n_clientes
        self.tot_rodada = tot_rodada

        self.tot_entregue = 0
        self.tot_anotado = 0
        self.rodada = 0
        self.falta_beber = n_clientes

        self.lock_espera_todos_beberem = threading.Condition()
        self.lock_anotacao = threading.Condition()

    def espera_beberem(self):
        with self.lock_espera_todos_beberem:
            self.falta_beber -= 1
            if self.falta_beber == 0:
                self.falta_beber = self.n_clientes
                self.rodada += 1

                if self.rodada == self.tot_rodada:
                    logging.info("\nFim da Rodada")
                else:
                    self.tot_anotado = 0
                    logging.info(f"\nRodada {self.rodada + 1}")
                self.lock_espera_todos_beberem.notify_all()
            else:
                self.lock_espera_todos_beberem.wait()

    def fechou(self):
        return self.rodada == self.tot_rodada

    def pedir(self, cliente):
        self.vazio.acquire()
        with self.lock:
            self.buff_quer.append(cliente)
        self.cheio.release()

    def anotar_pedido(self, garcom):
        with self.lock_anotacao:
            if self.tot_anotado < self.n_clientes:
                self.cheio.acquire()
                with self.lock:
                    if self.buff_quer:
                        cliente_atendido = self.buff_quer.popleft()
                    else:
                        cliente_atendido = self.buff_n_quer.popleft()

                    self.vazio.release()
                    self.tot_anotado += 1
                    return cliente_atendido
            return None

def parse_argumentos():
    parser = argparse.ArgumentParser()
    parser.add_argument('--n_clientes', type=int, default=5)
    parser.add_argument('--n_garcons', type=int, default=2)
    parser.add_argument('--cap_garcons', type=int, default=2)
    parser.add_argument('--n_rodadas', type=int, default=2)
    return parser.parse_args()

def main():
    args = parse_argumentos()

    logging.info(f"\nNumero de garcons: {args.n_garcons}\nNumero de Rodadas: {args.n_rodadas}\nNumero de Clientes: {args.n_clientes}\nCapacidade do Garcon: {args.cap_garcons}")
    logging.info("\n\nPrimeira Rodada")

    if args.n_clientes > 0:
        bartender = Bartender(args.n_clientes, args.n_rodadas)
        garcons = [Garcom(args.cap_garcons, i, bartender) for i in range(args.n_garcons)]
        clientes = [Cliente(i, bartender) for i in range(args.n_clientes)]

        for garcom in garcons:
            garcom.start()
        for cliente in clientes:
            cliente.start()

        for cliente in clientes:
            cliente.join()
        logging.info("\nClientes sairam.")
        for garcom in garcons:
            garcom.join()
    logging.info("\nGarcons param de servir.")
    logging.info("\nO bar fechou!")

if __name__ == '__main__':
    main()
