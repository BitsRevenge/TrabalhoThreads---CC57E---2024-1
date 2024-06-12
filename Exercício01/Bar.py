import threading
import time
import random
import collections
import logging
import argparse

logging.basicConfig(level=logging.DEBUG, format='%(message)s',)


class Cliente(threading.Thread):
    def __init__(self, nome, gerenciador):
        threading.Thread.__init__(self)
        self.nome = nome
        self.gerenciador = gerenciador
        self.e_esperar = threading.Event()
        self.beber = True

    def continuar(self):
        self.e_esperar.set()

    def faz_ped(self):
        self.beber = True
        self.gerenciador.pedir(self)

    def espera_ped(self):
        self.e_esperar.wait()
        self.e_esperar.clear()

    def recebe_ped(self):
        tempo = random.randint(1, 3)
        time.sleep(tempo)
        logging.info(" ".join(["Cliente",
                     str(self.nome),
                     "bebendo"]))

    def consome_ped(self):
        tempo = random.randint(1, 3)
        time.sleep(tempo)
        logging.info(" ".join(["Cliente",
                     str(self.nome),
                     "bebeu"]))
        self.gerenciador.espera_beberem()

    def run(self):
        while not self.gerenciador.fechou():
            self.faz_ped()
            if self.beber:
                self.espera_ped()
                self.recebe_ped()
                self.consome_ped()


class Garcom(threading.Thread):
    def __init__(self, max_cli, nome, gerenciador):
        threading.Thread.__init__(self)
        self.nome = nome
        self.gerenciador = gerenciador
        self.max_cli = max_cli
        self.anotados = []

    def recebe_max_ped(self):
        while len(self.anotados) < self.max_cli:
            cliente_atual = self.gerenciador.anotar_pedido(self)
            if cliente_atual is not None:
                if cliente_atual.beber:
                    self.anotados.append(cliente_atual)
                    logging.info(" ".join(["garcom",
                                 str(self.nome),
                                 "recebeu o pedido do Cliente",
                                 str(cliente_atual.nome)]))
                else:
                    cliente_atual.continuar()
            else:
                break

    def registra_ped(self):
        if len(self.anotados) > 0:
            tempo = random.randint(1, 3)
            time.sleep(tempo)
            logging.info(" ".join(["garcom",
                         str(self.nome),
                         "registrou os pedidos dos clientes",
                         str([i.nome for i in self.anotados])]))

    def entrega_ped(self):
        for i in self.anotados:
            i.continuar()  # e_esperar.set()
            logging.info(" ".join(["garcom",
                         str(self.nome),
                         "entregou para o Cliente",
                         str(i.nome)]))

        self.anotados.clear()

    def run(self):
        while not self.gerenciador.fechou():
            self.recebe_max_ped()
            self.registra_ped()
            self.entrega_ped()


class Gerenciador():
    def __init__(self, n_clientes, tot_rodada):
        self.buff_quer = collections.deque([], 1)
        self.buff_n_quer = collections.deque([], 1)
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
            if self.falta_beber - 1 == 0:
                self.falta_beber = self.n_clientes
                self.rodada += 1

                if self.rodada == self.tot_rodada:
                    logging.info("\nFim da Rodada")
                else:
                    self.tot_anotado = 0
                    logging.info(" ".join(["\nRodada ",
                                           str(self.rodada + 1)]))
                self.lock_espera_todos_beberem.notify_all()
            else:
                self.falta_beber -= 1
                self.lock_espera_todos_beberem.wait()

    def fechou(self):
        return self.tot_rodada == self.rodada

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
                    if len(self.buff_quer) == 1:
                        cliente_atendido = self.buff_quer.popleft()
                    else:
                        cliente_atendido = self.buff_n_quer.popleft()

                    self.vazio.release()
                    self.tot_anotado += 1
                    return cliente_atendido
            else:
                return None


def parse_argumentos():
    parser = argparse.ArgumentParser()
    parser.add_argument('--n_clientes',
                        help='Numero de Clientes. [10]',
                        type=int,
                        default=10)
    parser.add_argument('--n_garcons',
                        help='Numero de garcons. [2]',
                        type=int,
                        default=2)
    parser.add_argument('--cap_garcons',
                        help='Capacidade dos garcons. [3]',
                        type=int,
                        default=3)
    parser.add_argument('--n_rodadas',
                        help='Numero de rodadas. [2]',
                        type=int,
                        default=2)
    return parser.parse_args()


def main():
    args = parse_argumentos()

    logging.info(" ".join(["\nNumero de garcons",
                           str(args.n_garcons),
                           "\nNumero de Rodadas ",
                           str(args.n_rodadas),
                           "\nNumero de Clientes ",
                           str(args.n_clientes),
                           "\nCapacidade do Garcon",
                           str(args.cap_garcons)]))
    logging.info("\n\nPrimeira Rodada")

    if args.n_clientes > 0:
        geren = Gerenciador(args.n_clientes, args.n_rodadas)

        l_garcons = [Garcom(args.cap_garcons, i, geren)
                     for i in range(args.n_garcons)]
        l_clientes = [Cliente(i, geren) for i in range(args.n_clientes)]

        for i in l_garcons:
            i.start()
        for i in l_clientes:
            i.start()

        for i in l_clientes:
            i.join()
        logging.info("\nClientes sairam.")
        for i in l_garcons:
            i.join()
    logging.info("\nGarcons param de servir.")
    logging.info("\nO bar fechou !")

if __name__ == '__main__':
    main()