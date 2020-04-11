package zio.prelude

import zio.{ Schedule, ZIO, ZLayer, ZManaged, ZQueue, ZRef }
import zio.stream.{ ZSink, ZStream }

/**
 * `Contravariant[F]` provides implicit evidence that `F[-_]` is a
 * contravariant enfofunctor in the category of Scala objects.
 *
 * `Contravariant` instances of type `F[A]` "consume" values of type `A` in
 * some sense. For example, `Equal[A]` takes two values of type `A` as input
 * and returns a `Boolean` indicating whether they are equal. Similarly, a
 * `Ord[A]` takes two values of type `A` as input and returns an `Ordering`
 * with the result of comparing them and `Hash` takes an `A` value and returns
 * an `Int`.
 *
 * Common examples of contravariant instances in ZIO include effects with
 * regard to their environment types, sinks with regard to their input type,
 * and polymorphic queues and references regarding their input types.
 *
 * `Contravariant` instances support a `contramap` operation, which allows
 * transforming the input type given a function from the new input type to the
 * old input type. For example, if we have an `Ord[Int]` that allows us to
 * compare two integers and we have a function `String => Int` that returns
 * the length of a string, then we can construct an `Ord[String]` that
 * compares strings by computing their lengths with the provided function and
 * comparing those.
 */
trait Contravariant[F[-_]] {

  /**
   * Lift a function from `A` to `B` to a function from `F[B]` to `F[A]`.
   */
  def contramap[A, B](f: B => A): F[A] => F[B]

  /**
   * Contramapping with the identity function must be an identity function.
   */
  def identityLaw[A](fa: F[A])(implicit equal: Equal[F[A]]): Boolean =
    contramap(identity[A](_))(fa) === fa

  /**
   * Contramapping by `f` followed by `g` must be the same as contramapping
   * with the composition of `f` and `g`.
   */
  def compositionLaw[A, B, C](fa: F[A], f: C => B, g: B => A)(implicit equal: Equal[F[C]]): Boolean =
    contramap(f)(contramap(g)(fa)) === contramap(f andThen g)(fa)
}
object Contravariant {

  /**
   * Summons an implicit `Contravariant[F]`.
   */
  def apply[F[-_]](implicit contravariant: Contravariant[F]): Contravariant[F] =
    contravariant

  /**
   * The contravariant instance for `Function1`.
   */
  implicit def Function1Contravariant[B]: Contravariant[({ type lambda[-x] = x => B })#lambda] =
    new Contravariant[({ type lambda[-x] = x => B })#lambda] {
      def contramap[A, C](function: C => A): (A => B) => (C => B) =
        apply => a => apply(function(a))
    }

  /**
   * The contravariant instance for `Function2`.
   */
  implicit def Function2Contravariant[B, C]: Contravariant[({ type lambda[-x] = (x, B) => C })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B) => C })#lambda] {
      def contramap[A, D](function: D => A): ((A, B) => C) => ((D, B) => C) =
        apply => (a, b) => apply(function(a), b)
    }

  /**
   * The contravariant instance for `Function3`.
   */
  implicit def Function3Contravariant[B, C, D]: Contravariant[({ type lambda[-x] = (x, B, C) => D })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C) => D })#lambda] {
      def contramap[A, E](function: E => A): ((A, B, C) => D) => ((E, B, C) => D) =
        apply => (a, b, c) => apply(function(a), b, c)
    }

  /**
   * The contravariant instance for `Function4`.
   */
  implicit def Function4Contravariant[B, C, D, E]: Contravariant[({ type lambda[-x] = (x, B, C, D) => E })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D) => E })#lambda] {
      def contramap[A, F](function: F => A): ((A, B, C, D) => E) => ((F, B, C, D) => E) =
        apply => (a, b, c, d) => apply(function(a), b, c, d)
    }

  /**
   * The contravariant instance for `Function5`.
   */
  implicit def Function5Contravariant[B, C, D, E, F]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E) => F })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E) => F })#lambda] {
      def contramap[A, G](function: G => A): ((A, B, C, D, E) => F) => ((G, B, C, D, E) => F) =
        apply => (a, b, c, d, e) => apply(function(a), b, c, d, e)
    }

  /**
   * The contravariant instance for `Function6`.
   */
  implicit def Function6Contravariant[B, C, D, E, F, G]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F) => G })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F) => G })#lambda] {
      def contramap[A, H](function: H => A): ((A, B, C, D, E, F) => G) => ((H, B, C, D, E, F) => G) =
        apply => (a, b, c, d, e, f) => apply(function(a), b, c, d, e, f)
    }

  /**
   * The contravariant instance for `Function7`.
   */
  implicit def Function7Contravariant[B, C, D, E, F, G, H]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G) => H })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G) => H })#lambda] {
      def contramap[A, I](function: I => A): ((A, B, C, D, E, F, G) => H) => ((I, B, C, D, E, F, G) => H) =
        apply => (a, b, c, d, e, f, g) => apply(function(a), b, c, d, e, f, g)
    }

  /**
   * The contravariant instance for `Function8`.
   */
  implicit def Function8Contravariant[B, C, D, E, F, G, H, I]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H) => I })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H) => I })#lambda] {
      def contramap[A, J](function: J => A): ((A, B, C, D, E, F, G, H) => I) => ((J, B, C, D, E, F, G, H) => I) =
        apply => (a, b, c, d, e, f, g, h) => apply(function(a), b, c, d, e, f, g, h)
    }

  /**
   * The contravariant instance for `Function9`.
   */
  implicit def Function9Contravariant[B, C, D, E, F, G, H, I, J]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I) => J })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I) => J })#lambda] {
      def contramap[A, K](function: K => A): ((A, B, C, D, E, F, G, H, I) => J) => ((K, B, C, D, E, F, G, H, I) => J) =
        apply => (a, b, c, d, e, f, g, h, i) => apply(function(a), b, c, d, e, f, g, h, i)
    }

  /**
   * The contravariant instance for `Function10`.
   */
  implicit def Function10Contravariant[B, C, D, E, F, G, H, I, J, K]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J) => K })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J) => K })#lambda] {
      def contramap[A, L](
        function: L => A
      ): ((A, B, C, D, E, F, G, H, I, J) => K) => ((L, B, C, D, E, F, G, H, I, J) => K) =
        apply => (a, b, c, d, e, f, g, h, i, j) => apply(function(a), b, c, d, e, f, g, h, i, j)
    }

  /**
   * The contravariant instance for `Function11`.
   */
  implicit def Function11Contravariant[B, C, D, E, F, G, H, I, J, K, L]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K) => L })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K) => L })#lambda] {
      def contramap[A, M](
        function: M => A
      ): ((A, B, C, D, E, F, G, H, I, J, K) => L) => ((M, B, C, D, E, F, G, H, I, J, K) => L) =
        apply => (a, b, c, d, e, f, g, h, i, j, k) => apply(function(a), b, c, d, e, f, g, h, i, j, k)
    }

  /**
   * The contravariant instance for `Function12`.
   */
  implicit def Function12Contravariant[B, C, D, E, F, G, H, I, J, K, L, M]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L) => M })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L) => M })#lambda] {
      def contramap[A, N](
        function: N => A
      ): ((A, B, C, D, E, F, G, H, I, J, K, L) => M) => ((N, B, C, D, E, F, G, H, I, J, K, L) => M) =
        apply => (a, b, c, d, e, f, g, h, i, j, k, l) => apply(function(a), b, c, d, e, f, g, h, i, j, k, l)
    }

  /**
   * The contravariant instance for `Function13`.
   */
  implicit def Function13Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M) => N })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M) => N })#lambda] {
      def contramap[A, O](
        function: O => A
      ): ((A, B, C, D, E, F, G, H, I, J, K, L, M) => N) => ((O, B, C, D, E, F, G, H, I, J, K, L, M) => N) =
        apply => (a, b, c, d, e, f, g, h, i, j, k, l, m) => apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m)
    }

  /**
   * The contravariant instance for `Function14`.
   */
  implicit def Function14Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N) => O })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N) => O })#lambda] {
      def contramap[A, P](
        function: P => A
      ): ((A, B, C, D, E, F, G, H, I, J, K, L, M, N) => O) => ((P, B, C, D, E, F, G, H, I, J, K, L, M, N) => O) =
        apply => (a, b, c, d, e, f, g, h, i, j, k, l, m, n) => apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n)
    }

  /**
   * The contravariant instance for `Function15`.
   */
  implicit def Function15Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O) => P })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O) => P })#lambda] {
      def contramap[A, Q](
        function: Q => A
      ): ((A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) => P) => ((Q, B, C, D, E, F, G, H, I, J, K, L, M, N, O) => P) =
        apply =>
          (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) => apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n, o)
    }

  /**
   * The contravariant instance for `Function16`.
   */
  implicit def Function16Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) => Q })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) => Q })#lambda] {
      def contramap[A, R](function: R => A): (
        (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) => Q
      ) => ((R, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) => Q) =
        apply =>
          (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) =>
            apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
    }

  /**
   * The contravariant instance for `Function17`.
   */
  implicit def Function17Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) => R })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) => R })#lambda] {
      def contramap[A, S](function: S => A): (
        (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) => R
      ) => ((S, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) => R) =
        apply =>
          (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) =>
            apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
    }

  /**
   * The contravariant instance for `Function18`.
   */
  implicit def Function18Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) => S })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) => S })#lambda] {
      def contramap[A, T](function: T => A): (
        (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) => S
      ) => ((T, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) => S) =
        apply =>
          (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) =>
            apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
    }

  /**
   * The contravariant instance for `Function10`.
   */
  implicit def Function19Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) => T })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) => T })#lambda] {
      def contramap[A, U](function: U => A): (
        (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) => T
      ) => ((U, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) => T) =
        apply =>
          (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s) =>
            apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
    }

  /**
   * The contravariant instance for `Function20`.
   */
  implicit def Function20Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
    : Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) => U })#lambda] =
    new Contravariant[({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) => U })#lambda] {
      def contramap[A, V](function: V => A): (
        (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) => U
      ) => ((V, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) => U) =
        apply =>
          (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) =>
            apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
    }

  /**
   * The contravariant instance for `Function21`.
   */
  implicit def Function21Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]: Contravariant[
    ({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) => V })#lambda
  ] =
    new Contravariant[
      ({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) => V })#lambda
    ] {
      def contramap[A, W](function: W => A): (
        (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) => V
      ) => ((W, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) => V) =
        apply =>
          (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u) =>
            apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
    }

  /**
   * The contravariant instance for `Function22`.
   */
  implicit def Function22Contravariant[B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W]: Contravariant[
    ({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) => W })#lambda
  ] =
    new Contravariant[
      ({ type lambda[-x] = (x, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) => W })#lambda
    ] {
      def contramap[A, X](function: X => A): (
        (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) => W
      ) => ((X, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) => W) =
        apply =>
          (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) =>
            apply(function(a), b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)
    }

  /**
   * The contravariant instance for `Schedule`.
   */
  implicit def ScheduleContravariant[R, B]: Contravariant[({ type lambda[-x] = Schedule[R, x, B] })#lambda] =
    new Contravariant[({ type lambda[-x] = Schedule[R, x, B] })#lambda] {
      def contramap[A, A0](f: A0 => A): Schedule[R, A, B] => Schedule[R, A0, B] =
        schedule => schedule.contramap(f)
    }

  /**
   * The contravariant instance for `ZIO`.
   */
  implicit def ZIOContravariant[E, A]: Contravariant[({ type lambda[-x] = ZIO[x, E, A] })#lambda] =
    new Contravariant[({ type lambda[-x] = ZIO[x, E, A] })#lambda] {
      def contramap[R, R0](f: R0 => R): ZIO[R, E, A] => ZIO[R0, E, A] =
        zio => zio.provideSome(f)
    }

  /**
   * The contravariant instance for `ZLayer`.
   */
  implicit def ZLayerContravariant[E, ROut]: Contravariant[({ type lambda[-x] = ZLayer[x, E, ROut] })#lambda] =
    new Contravariant[({ type lambda[-x] = ZLayer[x, E, ROut] })#lambda] {
      def contramap[RIn, RIn0](f: RIn0 => RIn): ZLayer[RIn, E, ROut] => ZLayer[RIn0, E, ROut] =
        layer => ZLayer.fromFunctionMany(f) >>> layer
    }

  /**
   * The contravariant instance for `ZManaged`.
   */
  implicit def ZManagedContravariant[E, A]: Contravariant[({ type lambda[-x] = ZManaged[x, E, A] })#lambda] =
    new Contravariant[({ type lambda[-x] = ZManaged[x, E, A] })#lambda] {
      def contramap[R, R0](f: R0 => R): ZManaged[R, E, A] => ZManaged[R0, E, A] =
        managed => managed.provideSome(f)
    }

  /**
   * The contravariant instance for `ZQueue`.
   */
  implicit def ZQueueContravariant[RA, EA, RB, EB, A, B]
    : Contravariant[({ type lambda[-x] = ZQueue[RA, EA, RB, EB, x, B] })#lambda] =
    new Contravariant[({ type lambda[-x] = ZQueue[RA, EA, RB, EB, x, B] })#lambda] {
      def contramap[A, C](f: C => A): ZQueue[RA, EA, RB, EB, A, B] => ZQueue[RA, EA, RB, EB, C, B] =
        queue => queue.contramap(f)
    }

  /**
   * The contravariant instance for `ZRef`.
   */
  implicit def ZRefContravariant[EA, EB, B]: Contravariant[({ type lambda[-x] = ZRef[EA, EB, x, B] })#lambda] =
    new Contravariant[({ type lambda[-x] = ZRef[EA, EB, x, B] })#lambda] {
      def contramap[A, C](f: C => A): ZRef[EA, EB, A, B] => ZRef[EA, EB, C, B] =
        ref => ref.contramap(f)
    }

  /**
   * The contravariant instance for `ZSink`.
   */
  implicit def ZSinkContravariant[R, E, A0, B]: Contravariant[({ type lambda[-x] = ZSink[R, E, A0, x, B] })#lambda] =
    new Contravariant[({ type lambda[-x] = ZSink[R, E, A0, x, B] })#lambda] {
      def contramap[A, C](f: C => A): ZSink[R, E, A0, A, B] => ZSink[R, E, A0, C, B] =
        sink => sink.contramap(f)
    }

  /**
   * The contravariant instance for `ZStream`.
   */
  implicit def ZStreamContravariant[E, A]: Contravariant[({ type lambda[-x] = ZStream[x, E, A] })#lambda] =
    new Contravariant[({ type lambda[-x] = ZStream[x, E, A] })#lambda] {
      def contramap[R, R0](f: R0 => R): ZStream[R, E, A] => ZStream[R0, E, A] =
        stream => stream.provideSome(f)
    }
}
