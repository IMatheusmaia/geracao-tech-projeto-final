import { useState } from 'react';
import { useNavigate } from '@tanstack/react-router';
import { useMutation } from '@tanstack/react-query';
import { loginRequest } from '../../api/auth';
import { useAuth } from '../../hooks/useAuth';
import { loginSchema, type LoginFormData } from '../../schemas/auth';
import styles from './LoginPage.module.css';

export function LoginPage() {
  const navigate = useNavigate();
  const { setAuth } = useAuth();
  const [formError, setFormError] = useState('');

  const loginMutation = useMutation({
    mutationFn: loginRequest,
    onSuccess: (response) => {
      setAuth(response);
      void navigate({ to: '/admin' });
    },
    onError: () => {
      setFormError('Email ou senha inválidos.');
    },
  });

  function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setFormError('');

    const formData = new FormData(e.currentTarget);
    const raw: LoginFormData = {
      email: formData.get('email') as string,
      password: formData.get('password') as string,
    };

    const result = loginSchema.safeParse(raw);
    if (!result.success) {
      setFormError(result.error.issues[0].message);
      return;
    }

    loginMutation.mutate(result.data);
  }

  return (
    <div className={styles.page}>
      <form className={styles.form} onSubmit={handleSubmit}>
        <h1>Entrar</h1>
        {formError && <p className={styles.error}>{formError}</p>}
        <input
          name="email"
          type="email"
          placeholder="Email"
          required
        />
        <input
          name="password"
          type="password"
          placeholder="Senha"
          required
        />
        <button
          type="submit"
          className={styles.submitBtn}
          disabled={loginMutation.isPending}
        >
          {loginMutation.isPending ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
    </div>
  );
}
