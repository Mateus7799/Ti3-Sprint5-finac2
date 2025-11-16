# Documentação do Endpoint de Aniversariantes

## Endpoint

```
GET /api/aniversariantes/semana
```

## Descrição

Retorna uma lista de todos os aniversariantes da semana atual (de segunda-feira a domingo), incluindo tanto **Pessoas** quanto **Funcionários**.

## Autenticação

- Requer autenticação (Bearer Token)

## Parâmetros

Nenhum parâmetro necessário.

## Resposta

### Status Code
- `200 OK` - Requisição bem-sucedida

### Corpo da Resposta

```typescript
type AniversarianteResponseDTO = {
  id: number;
  nome: string;
  dataNascimento: Date;
  contato?: string;
  souFuncionario: boolean;
}

// Retorno
AniversarianteResponseDTO[]
```

### Exemplo de Resposta

```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "dataNascimento": "1990-11-17",
    "contato": "(11) 98765-4321",
    "souFuncionario": false
  },
  {
    "id": 5,
    "nome": "Maria Santos",
    "dataNascimento": "1985-11-18",
    "contato": null,
    "souFuncionario": true
  },
  {
    "id": 3,
    "nome": "Pedro Oliveira",
    "dataNascimento": "1992-11-20",
    "contato": "(11) 91234-5678",
    "souFuncionario": false
  }
]
```

## Campos

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `id` | number | Sim | ID único do aniversariante |
| `nome` | string | Sim | Nome completo do aniversariante |
| `dataNascimento` | Date | Sim | Data de nascimento |
| `contato` | string | Não | Telefone de contato (apenas para Pessoas) |
| `souFuncionario` | boolean | Sim | `true` se for funcionário, `false` se for pessoa |

## Regras de Negócio

1. **Semana Atual**: A semana é calculada de segunda-feira a domingo
2. **Data de Referência**: Usa a data atual do servidor
3. **Funcionários**: Apenas funcionários **ativos** (status = 1) são incluídos
4. **Ordenação**: A lista é ordenada por data de nascimento (mês e dia)
5. **Semanas entre Meses**: Trata corretamente semanas que cruzam dois meses diferentes
6. **Contato**: Funcionários não possuem campo de contato (retorna `null`)

## Exemplos de Uso

### JavaScript/TypeScript (Fetch API)

```typescript
async function buscarAniversariantesSemana() {
  try {
    const response = await fetch('/api/aniversariantes/semana', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error('Erro ao buscar aniversariantes');
    }
    
    const aniversariantes = await response.json();
    return aniversariantes;
  } catch (error) {
    console.error('Erro:', error);
    throw error;
  }
}
```

### React com TypeScript

```tsx
import { useEffect, useState } from 'react';

type Aniversariante = {
  id: number;
  nome: string;
  dataNascimento: Date;
  contato?: string;
  souFuncionario: boolean;
}

function AniversariantesList() {
  const [aniversariantes, setAniversariantes] = useState<Aniversariante[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('/api/aniversariantes/semana', {
      headers: {
        'Authorization': `Bearer ${token}`,
      }
    })
      .then(res => res.json())
      .then(data => {
        setAniversariantes(data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Erro ao buscar aniversariantes:', err);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Carregando...</div>;

  return (
    <div>
      <h2>Aniversariantes da Semana</h2>
      {aniversariantes.length === 0 ? (
        <p>Nenhum aniversariante esta semana</p>
      ) : (
        <ul>
          {aniversariantes.map(aniv => (
            <li key={`${aniv.souFuncionario ? 'f' : 'p'}-${aniv.id}`}>
              <strong>{aniv.nome}</strong>
              {aniv.souFuncionario && ' 👔 (Funcionário)'}
              <br />
              Aniversário: {new Date(aniv.dataNascimento).toLocaleDateString()}
              {aniv.contato && (
                <>
                  <br />
                  Contato: {aniv.contato}
                </>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
```

### Axios

```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

async function getAniversariantesSemana() {
  const { data } = await api.get('/aniversariantes/semana');
  return data;
}
```

## Tratamento de Erros

| Status Code | Descrição |
|-------------|-----------|
| 200 | Sucesso |
| 401 | Não autenticado |
| 403 | Sem permissão |
| 500 | Erro interno do servidor |

## Notas

- O endpoint substitui o antigo `/api/pessoa/aniversariantes-semana`
- Agora inclui funcionários além de pessoas
- Use o campo `souFuncionario` para diferenciar o tipo de aniversariante
- Funcionários podem ter `contato: null`

