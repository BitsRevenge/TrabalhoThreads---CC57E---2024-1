import threading
import time
import random

class Filosofo(threading.Thread):
    def __init__(self, nome, hashi_esquerdo, hashi_direito):
        threading.Thread.__init__(self)
        self.nome = nome
        self.hashi_esquerdo = hashi_esquerdo
        self.hashi_direito = hashi_direito

    def meditar(self):
        print(f'{self.nome} está meditando.')
        time.sleep(random.uniform(1, 3))

    def comer(self):
        print(f'{self.nome} está comendo.')
        time.sleep(random.uniform(2, 5))

    def run(self):
        while True:
            self.meditar()
            self.hashi_esquerdo.acquire()
            print(f'{self.nome} pegou o hashi esquerdo.')
            self.hashi_direito.acquire()
            print(f'{self.nome} pegou o hashi direito.')
            self.comer()
            self.hashi_esquerdo.release()
            print(f'{self.nome} largou o hashi esquerdo.')
            self.hashi_direito.release()
            print(f'{self.nome} largou o hashi direito.')

def main():
    hashi = [threading.Semaphore(1) for _ in range(5)]
    nomes_filosofos = ['f0', 'f1', 'f2', 'f3', 'f4']
    filosofos = []

    for i in range(5):
        hashi_esquerdo = hashi[i]
        hashi_direito = hashi[(i + 1) % 5]
        filosofo = Filosofo(nomes_filosofos[i], hashi_esquerdo, hashi_direito)
        filosofos.append(filosofo)

    for filosofo in filosofos:
        filosofo.start()

if __name__ == '__main__':
    main()